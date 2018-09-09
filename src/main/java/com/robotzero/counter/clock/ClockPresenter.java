package com.robotzero.counter.clock;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.domain.clock.CurrentClockState;
import com.robotzero.counter.event.*;
import com.robotzero.counter.event.action.Action;
import com.robotzero.counter.event.action.ActionType;
import com.robotzero.counter.event.action.ClickAction;
import com.robotzero.counter.event.action.TickAction;
import com.robotzero.counter.event.result.*;
import com.robotzero.counter.service.*;
import io.reactivex.*;
import io.reactivex.Observable;
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
import javafx.scene.text.Text;
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
        Map<ColumnType, ArrayList<Integer>> initialValues = this.clockService.initialize(DirectionType.DOWN);
        IntStream.rangeClosed(0, 3).forEach(index -> {
            this.timerColumns.get(ColumnType.SECONDS).setLabels(index, initialValues.get(ColumnType.SECONDS).get(index));
            this.timerColumns.get(ColumnType.MINUTES).setLabels(index, initialValues.get(ColumnType.MINUTES).get(index));
            this.timerColumns.get(ColumnType.HOURS).setLabels(index, initialValues.get(ColumnType.HOURS).get(index));
        });

//        optionClicks.subscribe(click -> stageController.setView());

//        resetClicks.subscribe(click -> {
//            this.fetchFromDatabase.setValue(true);
//            if (fetchFromDatabase.get()) {
//                this.localTimeClock.initializeClocks(Optional.ofNullable(savedTimerRepository.selectLatest()).orElseGet(() -> {
////                this.localTimeClock.initializeClocks(Optional.ofNullable(savedTimerRepository.selectLatest()).orElseGet(() -> {
//                    LocalTimeClock savedTimer = new LocalTimeClock();
//                    savedTimer.setSavedTimer(LocalTime.of(0, 0, 0));
//                    return savedTimer;
//                }).getSavedTimer());
//            } else {
//                this.localTimeClock.initializeClocks(LocalTime.of(0, 0, 0));
//            }
//            this.deltaStreamSeconds.onNext(0);
//            this.deltaStreamMinutes.onNext(0);
//            this.deltaStreamHours.onNext(0);
//            timerColumns.forEach((columnType, column) -> {
//                column.setLabels();
//            });
//            timerColumns.get(ColumnType.SECONDS).setLabels();
//        });

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
                .window(5)
                .flatMapSingle(scrollEventObservable -> scrollEventObservable.reduce(new Object(), (reducedScrollEvent, nextScrollEvent) -> nextScrollEvent))
                .cast(javafx.scene.input.ScrollEvent.class)
                .map(scrollMouseEvent -> {
                    return new ScrollEvent(((Node) scrollMouseEvent.getSource()).getId(), scrollMouseEvent.getDeltaY());
                });


        Observable<TickEvent> tickEvent = timerService.getTimer().map(elapsedTime -> new TickEvent(elapsedTime));

        Observable<MainViewEvent> mainViewEvents = Observable.merge(startClickEvent, resetClickEvent, scrollEvent, tickEvent);

        ObservableTransformer<ClickEvent, Action> clickEventTransformer = clickEvent -> clickEvent.flatMap(
          event -> {
              return resetService.getActions(event.getButtonType(), event.getButtonState());
          });

        ObservableTransformer<ScrollEvent, Action> scrollEventTransformer = scrollMouseEvent -> scrollMouseEvent.flatMap(
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

        ObservableTransformer<ClickAction, ClickResult> clickActionTransformer = clickAction -> clickAction.flatMap(action -> {
            return timerService.operateTimer(action);
        }).map(response -> new ClickResult(response.getActionType(), response.getNewButtonState()));

        ObservableTransformer<CurrentClockState, CurrentClockState> currentClockStateTickResultObservableTransformer = currentClockStateObservable -> currentClockStateObservable.publish(currentClockState -> currentClockState).flatMap(currentClockState -> {
            return Single.just(currentClockState).toObservable();
        });

        ObservableTransformer<TickAction, TickResult> tickActionTransformer = tickAction -> {
            return tickAction.flatMap(action -> {
                Observable<ChangeCell> secondsChangeCell = timerColumns.get(ColumnType.SECONDS).getChangeCell();
                Observable<ChangeCell> minutesChangeCell = timerColumns.get(ColumnType.MINUTES).getChangeCell();
                Observable<ChangeCell> hoursChangeCell = timerColumns.get(ColumnType.HOURS).getChangeCell();

                return Observable.zip(secondsChangeCell, minutesChangeCell, hoursChangeCell, (secondsCell, minutesCell, hoursCell) -> {
                   return clockService.tick(action, List.of(secondsCell, minutesCell, hoursCell));
                }).zipWith(currentClockStatePublishSubject.compose(currentClockStateTickResultObservableTransformer), (completableObservable, currentClockStateObservable) -> {
                    return currentClockStateObservable;
                }).flatMap(currentClockState -> {
                    return Observable.just(
                            new TickResult(
                                    currentClockState.getLabelSeconds(),
                                    currentClockState.getLabelMinutes(),
                                    currentClockState.getLabelHours(),
                                    currentClockState,
                                    action.getColumnType(),
                                    action.getTimerType()
                            )
                    );
                });
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
                    Optional<Text> secondsLabel = Optional.ofNullable(tickResult.getSecondsCell());
                    secondsLabel.ifPresent(textProperty -> {
                        textProperty.textProperty().setValue(String.format("%02d", tickResult.getLabels().getSecond()));
                        timerColumns.get(tickResult.getColumnType()).play(tickResult.getLabels().getDirectionSeconds().getDirectionType(), tickResult.getDuration());
                    });
                });
                Optional.of(tickResult).filter(result -> result.getLabels().shouldTickMinute()).ifPresent(result -> {
                    Optional<Text> minutesLabel = Optional.ofNullable(tickResult.getMinutesCell());
                    minutesLabel.ifPresent(textProperty -> {
                        textProperty.textProperty().setValue(String.format("%02d", tickResult.getLabels().getMinute()));
                        timerColumns.get(ColumnType.MINUTES).play(tickResult.getLabels().getDirectionMinutes().getDirectionType(), tickResult.getDuration());
                    });
                });
                Optional.of(tickResult).filter(result -> result.getLabels().shouldTickHour()).ifPresent(result -> {
                    Optional<Text> hoursLabel = Optional.ofNullable(tickResult.getHoursCell());
                    hoursLabel.ifPresent(textProperty -> {
                        textProperty.textProperty().setValue(String.format("%02d", tickResult.getLabels().getHour()));
                        timerColumns.get(ColumnType.HOURS).play(tickResult.getLabels().getDirectionHours().getDirectionType(), tickResult.getDuration());
                    });
                });
            });
        }, error -> {
            System.out.println(error.getMessage());
            throw new IOException("CRASHING THE APP...");
        });
    }
}
