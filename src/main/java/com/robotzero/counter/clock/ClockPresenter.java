package com.robotzero.counter.clock;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.domain.clock.CurrentClockState;
import com.robotzero.counter.event.*;
import com.robotzero.counter.event.action.Action;
import com.robotzero.counter.event.action.ActionType;
import com.robotzero.counter.event.action.ClickAction;
import com.robotzero.counter.event.action.TickAction;
import com.robotzero.counter.event.result.ClickResult;
import com.robotzero.counter.event.result.CurrentViewData;
import com.robotzero.counter.event.result.Result;
import com.robotzero.counter.event.result.TickResult;
import com.robotzero.counter.service.*;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;

public class ClockPresenter implements Initializable {

    @FXML
    GridPane gridPane;

    @FXML
    Button startButton, resetButton;

    @FXML
    StackPane seconds, minutes, hours;

    @Autowired
    private Populator populator;

    @Autowired
    private TimerService timerService;

    @Autowired
    private CellService cellStateService;

    // Temp options simulating options screen
    @Autowired
    private BooleanProperty fetchFromDatabase;

    @Autowired
    private StageController stageController;

    @Autowired
    private ClockService clockService;

    @Autowired
    private ResetService resetService;

    @Qualifier("clockState")
    @Autowired
    Observable<CurrentClockState> currentClockStatePublishSubject;

    private Map<ColumnType, Column> timerColumns;

    private BooleanProperty timerMute = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        startButton.textProperty().bind(new When(scrollMuteProperty.isEqualTo(new SimpleBooleanProperty(false))).then("Start").otherwise("Pause"));
        timerMute.bind(startButton.armedProperty());
        this.timerColumns = this.populator.timerColumns(this.gridPane);
        this.cellStateService.initialize(this.populator.cellState(gridPane));
        Map<ColumnType, ArrayList<Integer>> initialValues = this.clockService.initialize(DirectionType.DOWN);
        //@TODO initialize by sending the event.
        IntStream.rangeClosed(0, 3).forEach(index -> {
            this.timerColumns.get(ColumnType.SECONDS).setLabels(index, initialValues.get(ColumnType.SECONDS).get(index));
            this.timerColumns.get(ColumnType.MINUTES).setLabels(index, initialValues.get(ColumnType.MINUTES).get(index));
            this.timerColumns.get(ColumnType.HOURS).setLabels(index, initialValues.get(ColumnType.HOURS).get(index));
        });

//        optionClicks.subscribe(click -> stageController.setView());

        Observable<ClickEvent> startClickEvent = JavaFxObservable.eventsOf(startButton, MouseEvent.MOUSE_CLICKED)
                .observeOn(Schedulers.computation())
                .map(ignored -> new ClickEvent(ButtonType.LEFT, ButtonState.valueOf(startButton.getText().toUpperCase())));

        Observable<ClickEvent> resetClickEvent = JavaFxObservable.eventsOf(resetButton, MouseEvent.MOUSE_CLICKED)
                .observeOn(Schedulers.computation())
                .map(ignored -> new ClickEvent(ButtonType.RIGHT, ButtonState.valueOf(resetButton.getText().toUpperCase())));

        Observable<ScrollEvent> scrollEvent = JavaFxObservable.eventsOf(seconds, javafx.scene.input.ScrollEvent.SCROLL)
                .mergeWith(JavaFxObservable.eventsOf(minutes, javafx.scene.input.ScrollEvent.SCROLL))
                .mergeWith(JavaFxObservable.eventsOf(hours, javafx.scene.input.ScrollEvent.SCROLL))
                .observeOn(Schedulers.computation())
                .filter(event -> event.getDeltaY() != 0L)
                .window(5)
                .flatMapSingle(scrollEventObservable -> scrollEventObservable.reduce(new Object(), (reducedScrollEvent, nextScrollEvent) -> nextScrollEvent))
                .cast(javafx.scene.input.ScrollEvent.class)
                .map(scrollMouseEvent -> {
                    return new ScrollEvent(((Node) scrollMouseEvent.getSource()).getId(), scrollMouseEvent.getDeltaY());
                });


        Observable<TickEvent> tickEvent = timerService.getTimer().map(elapsedTime -> new TickEvent(elapsedTime));

        Observable<MainViewEvent> mainViewEvents = Observable.merge(startClickEvent, resetClickEvent, scrollEvent, tickEvent);

        ObservableTransformer<ClickEvent, Action> clickEventTransformer = clickEvent -> clickEvent.concatMap(
          event -> {
              return resetService.getActions(event.getButtonType(), event.getButtonState());
          });

        ObservableTransformer<ScrollEvent, Action> scrollEventTransformer = scrollMouseEvent -> scrollMouseEvent.concatMap(
                event -> {
                    return Observable.just(new TickAction(event.getDelta(), event.getColumnType(), TimerType.SCROLL));
                });

        ObservableTransformer<TickEvent, TickAction> tickEventTransformer = tickEv -> tickEv.flatMap(event -> {
            return Observable.just(new TickAction(-1, ColumnType.SECONDS, TimerType.TICK));
        });

        ObservableTransformer<MainViewEvent, Action> actionTransformer = mainViewEvent -> mainViewEvent.publish(sharedObservable -> {
            return Observable.merge(
              sharedObservable.ofType(ClickEvent.class).compose(clickEventTransformer),
              sharedObservable.ofType(ScrollEvent.class).compose(scrollEventTransformer),
              sharedObservable.ofType(TickEvent.class).compose(tickEventTransformer)
            );
        });

        ObservableTransformer<ClickAction, ClickResult> clickActionTransformer = clickAction -> clickAction.concatMap(action -> {
            return timerService.operateTimer(action);
        }).map(response -> new ClickResult(response.getActionType(), response.getNewButtonState()));

        ObservableTransformer<TickAction, TickResult> tickActionTransformer = tickAction -> {
            return tickAction.flatMap(action -> {
                return clockService.tick(action).andThen(currentClockStatePublishSubject.take(1).flatMap(currentClockState -> {
                    return Observable.just(new TickResult(currentClockState, action.getColumnType(), action.getTimerType()));
                }));
            });
        };

        Observable<Action> actions = mainViewEvents.compose(actionTransformer);
        Observable<Result> results = actions.publish(action -> {
            return Observable.merge(
                    action.ofType(ClickAction.class).compose(clickActionTransformer),
                    action.ofType(TickAction.class).compose(tickActionTransformer)
            );
        });

        Observable<CurrentViewState> uiModels = results.scan(CurrentViewState.idle(), (startingState, intermediateState) -> {
            if (intermediateState.getClass().equals(ClickResult.class)) {
                ClickResult clickResult = (ClickResult) intermediateState;
                if (clickResult.getActionType().equals(ActionType.LEFT)) {
                    if (clickResult.getButtonState().equals(ButtonState.START)) {
                        return CurrentViewState.pause(new CurrentViewData(clickResult, null, null));
                    }

                    if (clickResult.getButtonState().equals(ButtonState.PAUSE)) {
                        return CurrentViewState.start(new CurrentViewData(clickResult, null, null));
                    }

                    if (clickResult.getButtonState().equals(ButtonState.STOP)) {
                        return CurrentViewState.stop(new CurrentViewData(clickResult, null, null));
                    }
                }

                if (clickResult.getActionType().equals(ActionType.RIGHT)) {
                    return CurrentViewState.reset(new CurrentViewData(clickResult, null, null));
                }
            }

            if (intermediateState.getClass().equals(TickResult.class)) {
                TickResult tickResult = (TickResult) intermediateState;
                return CurrentViewState.tick(new CurrentViewData(null, null, tickResult));
            }

            return CurrentViewState.idle();
        });

        uiModels.observeOn(JavaFxScheduler.platform()).subscribe(currentViewState -> {
            Optional.of(currentViewState).filter(CurrentViewState::isClick).ifPresent(state -> {
                startButton.textProperty().setValue(state.getData().getClickResult().getButtonState().getDescription());
                Optional.of(state).filter(CurrentViewState::isStart).ifPresent(s -> {
                    resetButton.disableProperty().setValue(true);
                });
                Optional.of(state).filter(CurrentViewState::isPause).ifPresent(s -> {
                    resetButton.disableProperty().setValue(false);
                });
                Optional.of(state).filter(CurrentViewState::isStop).ifPresent(s -> {
                    resetButton.disableProperty().setValue(false);
                });
                Optional.of(state).filter(CurrentViewState::isReset).ifPresent(s -> {
                    startButton.textProperty().setValue("Start");
                    resetButton.disableProperty().setValue(false);
                });
            });

            Optional.of(currentViewState).filter(CurrentViewState::isTick).ifPresent(state -> {
                TickResult tickResult = currentViewState.getData().getTickResult();
                Optional.of(tickResult).filter(result -> result.getLabels().shouldTickSecond()).ifPresent(result -> {
                    List<CellState> cellStates = result.getLabels().getCellStates();
                    Optional<CellState> cellState = cellStates.stream().filter(cellS -> cellS.getColumnType() == ColumnType.SECONDS).findFirst();
                    cellState.ifPresent(cs -> {
                        int vboxId =  cs.getId();
                        Column column = this.timerColumns.get(ColumnType.SECONDS);
                        column.setLabel(vboxId, tickResult.getLabels().getSecond());
                        this.cellStateService.getAll(ColumnType.SECONDS).entrySet().stream().map(entry -> entry.getValue()).forEach(cellState1 -> {
                            column.play(cellState1, tickResult.getDuration());
                        });
                    });
                });
                Optional.of(tickResult).filter(result -> result.getLabels().shouldTickMinute()).ifPresent(result -> {
                    List<CellState> cellStates = result.getLabels().getCellStates();
                    Optional<CellState> cellState = cellStates.stream().filter(cellS -> cellS.getColumnType() == ColumnType.MINUTES).findFirst();
                    cellState.ifPresent(cs -> {
                        int vboxId =  cs.getId();
                        Column column = this.timerColumns.get(ColumnType.MINUTES);
                        column.setLabel(vboxId, tickResult.getLabels().getMinute());
                        this.cellStateService.getAll(ColumnType.MINUTES).entrySet().stream().map(entry -> entry.getValue()).forEach(cellState1 -> {
                            column.play(cellState1, tickResult.getDuration());
                        });
                    });
                });

                Optional.of(tickResult).filter(result -> result.getLabels().shouldTickHour()).ifPresent(result -> {
                    List<CellState> cellStates = result.getLabels().getCellStates();
                    Optional<CellState> cellState = cellStates.stream().filter(cellS -> cellS.getColumnType() == ColumnType.HOURS).findFirst();
                    cellState.ifPresent(cs -> {
                        int vboxId =  cs.getId();
                        Column column = this.timerColumns.get(ColumnType.HOURS);
                        column.setLabel(vboxId, tickResult.getLabels().getHour());
                        this.cellStateService.getAll(ColumnType.HOURS).entrySet().stream().map(entry -> entry.getValue()).forEach(cellState1 -> {
                            column.play(cellState1, tickResult.getDuration());
                        });
                    });
                });

            });
        }, error -> {
            System.out.println(error.getMessage());
            throw new IOException("CRASHING THE APP...");
        });
    }
}
