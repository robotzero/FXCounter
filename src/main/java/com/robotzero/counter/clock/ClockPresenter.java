package com.robotzero.counter.clock;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.event.*;
import com.robotzero.counter.event.action.*;
import com.robotzero.counter.event.result.*;
import com.robotzero.counter.service.*;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
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

    private Map<ColumnType, Column> timerColumns;

    private BooleanProperty timerMute = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timerMute.bind(startButton.armedProperty());

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
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .map(event -> {
                    return new ScrollEvent(((Node) event.getSource()).getId(), event.getDeltaY());
                });

        Observable<InitViewEvent> initViewEvent = JavaFxObservable.eventsOf(gridPane, EventType.ROOT)
                .filter(c -> c.getEventType().getName().toLowerCase().equals("stageshow"))
                .take(1)
                .map(event -> new InitViewEvent());

        Observable<TickEvent> tickEvent = timerService.getTimer().map(elapsedTime -> new TickEvent(elapsedTime));

        Observable<MainViewEvent> mainViewEvents = Observable.merge(Observable.merge(startClickEvent, resetClickEvent, scrollEvent, tickEvent), initViewEvent);

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

        ObservableTransformer<InitViewEvent, InitViewAction> initViewEventTransformer = initView -> initView.concatMap(event -> {
            return Observable.just(new InitViewAction());
        });

        ObservableTransformer<MainViewEvent, Action> actionTransformer = mainViewEvent -> mainViewEvent.publish(sharedObservable -> {
            return Observable.merge(Observable.merge(
              sharedObservable.ofType(ClickEvent.class).compose(clickEventTransformer),
              sharedObservable.ofType(ScrollEvent.class).compose(scrollEventTransformer),
              sharedObservable.ofType(TickEvent.class).compose(tickEventTransformer)
            ), sharedObservable.ofType(InitViewEvent.class).compose(initViewEventTransformer));
        });

        ObservableTransformer<InitViewAction, InitViewResult> initViewActionTransformer = initViewAction -> initViewAction.concatMap(action -> {
            Map<ColumnType, ArrayList<Integer>> initialValues = this.clockService.initialize(DirectionType.DOWN);
            return Observable.just(new InitViewResult(initialValues));
        });

        ObservableTransformer<ClickAction, ClickResult> clickActionTransformer = clickAction -> clickAction.concatMap(action -> {
            return timerService.operateTimer(action);
        }).map(response -> new ClickResult(response.getActionType(), response.getNewButtonState()));

        ObservableTransformer<TickAction, TickResult> tickActionTransformer = tickAction -> {
            return tickAction.flatMap(action -> {
                return clockService.tick(action).flatMap(currentClockState -> {
                    return Observable.just(new TickResult(currentClockState, action.getColumnType(), action.getTimerType()));
                });
            });
        };

        Observable<Action> actions = mainViewEvents.compose(actionTransformer);
        Observable<Result> results = actions.publish(action -> {
            return Observable.merge(
                    action.ofType(ClickAction.class).compose(clickActionTransformer),
                    action.ofType(TickAction.class).compose(tickActionTransformer),
                    action.ofType(InitViewAction.class).compose(initViewActionTransformer)
            );
        });

        Observable<CurrentViewState> uiModels = results.scan(CurrentViewState.idle(), (startingState, intermediateState) -> {
            if (intermediateState.getClass().equals(ClickResult.class)) {
                ClickResult clickResult = (ClickResult) intermediateState;
                if (clickResult.getActionType().equals(ActionType.LEFT)) {
                    if (clickResult.getButtonState().equals(ButtonState.START)) {
                        return CurrentViewState.pause(new CurrentViewData(clickResult, null, null, null));
                    }

                    if (clickResult.getButtonState().equals(ButtonState.PAUSE)) {
                        return CurrentViewState.start(new CurrentViewData(clickResult, null, null, null));
                    }

                    if (clickResult.getButtonState().equals(ButtonState.STOP)) {
                        return CurrentViewState.stop(new CurrentViewData(clickResult, null, null, null));
                    }
                }

                if (clickResult.getActionType().equals(ActionType.RIGHT)) {
                    return CurrentViewState.reset(new CurrentViewData(clickResult, null, null, null));
                }
            }

            if (intermediateState.getClass().equals(TickResult.class)) {
                TickResult tickResult = (TickResult) intermediateState;
                return CurrentViewState.tick(new CurrentViewData(null, null, tickResult, null));
            }

            if (intermediateState.getClass().equals(InitViewResult.class)) {
                InitViewResult initViewResult = (InitViewResult) intermediateState;
                return CurrentViewState.init(new CurrentViewData(null, null, null, initViewResult));
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
                List<CellState> cellStates = tickResult.getLabels().getCellStates();
                cellStates.stream().filter(cellS -> cellS.getColumnType() == ColumnType.SECONDS).findFirst().ifPresent(cs -> {
                    int vboxId =  cs.getId();
                    Column column = this.timerColumns.get(ColumnType.SECONDS);
                    column.setLabel(vboxId, tickResult.getLabels().getSecond());
                    this.cellStateService.getAll(ColumnType.SECONDS).forEach(cellState1 -> {
                        column.play(cellState1, tickResult.getDuration());
                    });
                });
                 cellStates.stream().filter(cellS -> cellS.getColumnType() == ColumnType.MINUTES).findFirst().ifPresent(cs -> {
                     int vboxId =  cs.getId();
                     Column column = this.timerColumns.get(ColumnType.MINUTES);
                     column.setLabel(vboxId, tickResult.getLabels().getMinute());
                     this.cellStateService.getAll(ColumnType.MINUTES).forEach(cellState1 -> {
                         column.play(cellState1, tickResult.getDuration());
                     });
                 });
                 cellStates.stream().filter(cellS -> cellS.getColumnType() == ColumnType.HOURS).findFirst().ifPresent(cs -> {
                     int vboxId =  cs.getId();
                     Column column = this.timerColumns.get(ColumnType.HOURS);
                     column.setLabel(vboxId, tickResult.getLabels().getHour());
                     this.cellStateService.getAll(ColumnType.HOURS).forEach(cellState1 -> {
                         column.play(cellState1, tickResult.getDuration());
                     });
                 });
            });

            Optional.of(currentViewState).filter(CurrentViewState::isInit).ifPresent(state -> {
                InitViewResult initResult = currentViewState.getData().getInitViewResult();
                this.timerColumns = populator.timerColumns(gridPane);
                this.cellStateService.initialize(this.populator.cellState(gridPane));
                IntStream.rangeClosed(0, 3).forEach(index -> {
                    this.timerColumns.get(ColumnType.SECONDS).setLabels(index, initResult.getInitialValues().get(ColumnType.SECONDS).get(index));
                    this.timerColumns.get(ColumnType.MINUTES).setLabels(index, initResult.getInitialValues().get(ColumnType.MINUTES).get(index));
                    this.timerColumns.get(ColumnType.HOURS).setLabels(index, initResult.getInitialValues().get(ColumnType.HOURS).get(index));
                });

            });
        }, error -> {
            System.out.println(error.getMessage());
            throw new IOException("CRASHING THE APP...");
        });
    }
}
