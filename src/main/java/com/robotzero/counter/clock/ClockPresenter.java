package com.robotzero.counter.clock;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.domain.clock.CurrentClockState;
import com.robotzero.counter.event.*;
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

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
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
    private DirectionService directionService;

    @Autowired
    private ClockService clockService;

    @Autowired
    private ResetService resetService;

//    @Autowired
//    private Flowable<CurrentClockState> clockState;

    private Map<ColumnType, Column> timerColumns;

    private BooleanProperty timerMute = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        startButton.textProperty().bind(new When(scrollMuteProperty.isEqualTo(new SimpleBooleanProperty(false))).then("Start").otherwise("Pause"));
        timerMute.bind(startButton.armedProperty());
        this.timerColumns = this.populator.timerColumns(this.gridPane);
        Map<ColumnType, ArrayList<Integer>> initialValues = this.clockService.initialize(Direction.DOWN);
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
                .buffer(3)
                .map(scrollMouseEvent -> new com.robotzero.counter.event.ScrollEvent(((Node) scrollMouseEvent.get(0).getSource()).getId(), scrollMouseEvent.get(0).getDeltaY()));


        Observable<TickEvent> tickEvent = timerService.getTimer().map(elapsedTime -> new TickEvent(elapsedTime));

        Observable<MainViewEvent> mainViewEvents = Observable.merge(startClickEvent, resetClickEvent, scrollEvent, tickEvent);

        ObservableTransformer<ClickEvent, com.robotzero.counter.event.action.Action> clickEventTransformer = clickEvent -> clickEvent.flatMap(
          event -> {
              return resetService.getActions(event.getButtonType(), event.getButtonState());
          });

        ObservableTransformer<ScrollEvent, com.robotzero.counter.event.action.Action> scrollEventTransformer = scrollMouseEvent -> scrollMouseEvent.flatMap(
                event -> {
                    return Observable.just(new TickAction(event.getDelta(), event.getColumnType(), TimerType.SCROLL));
                });

        ObservableTransformer<TickEvent, TickAction> tickEventTransformer = tickEv -> tickEv.flatMap(event -> {
            return Observable.just(new TickAction(-1, ColumnType.SECONDS, TimerType.TICK));
        });

        ObservableTransformer<MainViewEvent, com.robotzero.counter.event.action.Action> actionTransformer = mainViewEvent -> mainViewEvent.publish(sharedObservable -> {
            return Observable.merge(
              sharedObservable.ofType(ClickEvent.class).compose(clickEventTransformer),
              sharedObservable.ofType(ScrollEvent.class).compose(scrollEventTransformer),
              sharedObservable.ofType(TickEvent.class).compose(tickEventTransformer)
            );
        });

        ObservableTransformer<ClickAction, ClickResult> clickActionTransformer = clickAction -> clickAction.flatMap(action -> {
            return timerService.operateTimer(action);
        }).map(response -> new ClickResult(response.getActionType(), response.getNewButtonState()));

        ObservableTransformer<TickAction, TickResult> tickActionTransformer = tickAction -> {
            return tickAction.flatMap(action -> {
                Flowable<ChangeCell> changeCell = timerColumns.get(action.getColumnType()).getChangeCell();
                Flowable<Direction> observableDirection = changeCell.flatMapSingle(cell -> directionService.calculateDirection(cell.getTranslateY(), action.getDelta(), action.getColumnType()));
                Flowable<CurrentClockState> flowableCurrentClockState = observableDirection.flatMapSingle(direction -> {
                    return clockService.tick(direction, action, List.of(changeCell, timerColumns.get(ColumnType.MINUTES).getChangeCell(), timerColumns.get(ColumnType.HOURS).getChangeCell()));
                }).flatMap(currentClockState -> {
                    Flowable<CurrentClockState> minutesStateObservable = Single.just(currentClockState).toFlowable();
                    Flowable<CurrentClockState> hoursStateObservable = minutesStateObservable;
                    if (currentClockState.shouldTickMinute() && currentClockState.shouldTickSecond()) {
//                        minutesStateObservable = timerColumns.get(ColumnType.MINUTES).getChangeCell().flatMapSingle(cell -> directionService.calculateDirection(cell.getTranslateY(), action.getDelta(), ColumnType.MINUTES)).flatMapSingle(
//                                direction -> clockService.tick(direction, action.getTimerType(), ColumnType.MINUTES)
//                        );
                    }

                    if (currentClockState.shouldTickHour() && currentClockState.shouldTickSecond()) {
//                        hoursStateObservable = timerColumns.get(ColumnType.HOURS).getChangeCell().flatMapSingle(cell -> directionService.calculateDirection(cell.getTranslateY(), action.getDelta(), ColumnType.HOURS)).flatMapSingle(
//                                direction -> clockService.tick(direction, action.getTimerType(), ColumnType.HOURS)
//                        );
                    }

//                    return Flowable.merge(Flowable.just(currentClockState), minutesStateObservable, hoursStateObservable);
                    return Flowable.zip(
                            minutesStateObservable,
                            hoursStateObservable,
                            (minutesState, hoursState) -> {
                                return new CurrentClockState(currentClockState.getSecond(), minutesState.getMinute(), hoursState.getHour(), currentClockState.getDirection(), currentClockState.shouldTickSecond(), currentClockState.shouldTickMinute(), currentClockState.shouldTickHour());
                            }
                    );
                });

                return Flowable.zip(
                        action.getColumnType().equals(ColumnType.SECONDS) ? changeCell : timerColumns.get(ColumnType.SECONDS).getChangeCell(),
                        action.getColumnType().equals(ColumnType.MINUTES) ? changeCell : timerColumns.get(ColumnType.MINUTES).getChangeCell(),
                        action.getColumnType().equals(ColumnType.HOURS) ? changeCell : timerColumns.get(ColumnType.HOURS).getChangeCell(),
                        flowableCurrentClockState,
                        ((secondsChangeCell, minutesChangeCell, hoursChangeCell, currentClockState) -> {
                            return new TickResult(secondsChangeCell.getLabel(), minutesChangeCell.getLabel(), hoursChangeCell.getLabel(), currentClockState, action.getColumnType(), action.getTimerType());
                        })
                ).toObservable();
            });
        };

        Observable<com.robotzero.counter.event.action.Action> actions = mainViewEvents.compose(actionTransformer);

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
                    Text secondsLabel = tickResult.getSecondsCell();
                    secondsLabel.textProperty().setValue(String.format("%02d", tickResult.getLabels().getSecond()));
                    System.out.println(LocalDateTime.now() + "SECONDS");
                    timerColumns.get(tickResult.getColumnType()).play(tickResult.getLabels().getDirection(), tickResult.getDuration());
                });
                Optional.of(tickResult).filter(result -> result.getLabels().shouldTickMinute()).ifPresent(result -> {
                    Text minutesLabel = tickResult.getMinutesCell();
                    minutesLabel.textProperty().setValue(String.format("%02d", tickResult.getLabels().getMinute()));
                    System.out.println(LocalDateTime.now() + "MINUTES");
                    timerColumns.get(ColumnType.MINUTES).play(tickResult.getLabels().getDirection(), tickResult.getDuration());
                });
                Optional.of(tickResult).filter(result -> result.getLabels().shouldTickHour()).ifPresent(result -> {
//                    Cell hoursCell = tickResult.getHoursCell();
//                    hoursCell.setLabel(tickResult.getLabels().getHour());
//                    System.out.println(LocalDateTime.now() + "HOUR");
//                    timerColumns.get(hoursCell.getColumnType()).play(tickResult.getLabels().getDirection(), tickResult.getDuration());
                });
            });
        }, error -> {
            System.out.println(error.getMessage());
            throw new IOException("CRASHING THE APP...");
        });
    }
}
