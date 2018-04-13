package com.robotzero.counter.clock;

import com.robotzero.counter.domain.Cell;
import com.robotzero.counter.domain.Column;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.event.*;
import com.robotzero.counter.event.action.ActionType;
import com.robotzero.counter.event.action.ClickAction;
import com.robotzero.counter.event.action.ScrollAction;
import com.robotzero.counter.event.action.TickAction;
import com.robotzero.counter.event.result.*;
import com.robotzero.counter.service.*;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.reactfx.EventSource;
import org.reactfx.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public class ClockPresenter implements Initializable {

    @FXML
    GridPane gridPane;

    @FXML
    Button startButton, resetButton;

    @FXML
    StackPane seconds;
//
//    @FXML
//    StackPane paneMinutes;
//
//    @FXML
//    StackPane paneHours;

//    @FXML
//    Text textSeconds;
//
//    @FXML
//    Text textMinutes;
//
//    @FXML
//    Text textHours;
//
//    @FXML
//    Label optionsLabel;

    @Autowired
    private Populator populator;

//    @Autowired
//    private Clock localTimeClock;

    @Autowired
    private TimerService timerService;
//
//    @Autowired
//    private ClockRepository savedTimerRepository;

    // Temp options simulating options screen
    @Autowired
    private BooleanProperty fetchFromDatabase;

    @Autowired
    @Qualifier("PlayMinutes")
    private EventSource<Void> playMinutes;

    @Autowired
    @Qualifier("PlayHours")
    private EventSource<Void> playHours;

    @Autowired
    @Qualifier("StopCountdown")
    private EventSource<Void> stopCountdown;

    @Autowired
    @Qualifier("DeltaStreamSeconds")
    private Subject<Direction> deltaStreamSeconds;

    @Autowired
    @Qualifier("DeltaStreamMinutes")
    private Subject<Direction> deltaStreamMinutes;

    @Autowired
    @Qualifier("DeltaStreamHours")
    private Subject<Direction> deltaStreamHours;

    @Autowired
    private StageController stageController;

    @Autowired
    private DirectionService directionService;

    @Autowired
    private ClockService clockService;

    private BooleanProperty scrollMuteProperty = new SimpleBooleanProperty(false);

    private Subscription subscribe;

    private Map<ColumnType, Column> timerColumns;

    private BooleanProperty timerMute = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        startButton.textProperty().bind(new When(scrollMuteProperty.isEqualTo(new SimpleBooleanProperty(false))).then("Start").otherwise("Pause"));
        timerMute.bind(startButton.armedProperty());
        this.timerColumns = this.populator.timerColumns(this.gridPane);
        Map<ColumnType, Map<Integer, Integer>> initialValues = this.clockService.initialize(Direction.DOWN);
        IntStream.rangeClosed(0, 3).forEach(index -> {
            this.timerColumns.get(ColumnType.SECONDS).setLabels(index, initialValues.get(ColumnType.SECONDS).get(index));
            this.timerColumns.get(ColumnType.MINUTES).setLabels(index, initialValues.get(ColumnType.MINUTES).get(index));
            this.timerColumns.get(ColumnType.HOURS).setLabels(index, initialValues.get(ColumnType.HOURS).get(index));
        });
//        this.clockService.initialize(Direction.DOWN).get(ColumnType.SECONDS).get(0).

//        reset.disableProperty().bind(scrollMuteProperty);
//        EventStream<ScrollEvent> merged = EventStreams.merge(
//                EventStreams.eventsOf(paneSeconds, ScrollEvent.SCROLL).suppressWhen(timerColumns.get(ColumnType.SECONDS).isRunning()),
//                EventStreams.eventsOf(paneMinutes, ScrollEvent.SCROLL).suppressWhen(timerColumns.get(ColumnType.MINUTES).isRunning()),
//                EventStreams.eventsOf(paneHours, ScrollEvent.SCROLL).suppressWhen(timerColumns.get(ColumnType.HOURS).isRunning())
//        );

        // If startButton button is clicked mute scroll event until stop button is clicked.
//        StateMachine.init(scrollMuteProperty)
//                .on(startClicks).transition((wasMuted, event) -> {
//                    if (wasMuted.get()) {
//                        scrollMuteProperty.set(false);
//                        return scrollMuteProperty;
//                    }
//
//                    if (localTimeClock.getMainClock().compareTo(LocalTime.of(0, 0, 0)) == 0) {
//                        return scrollMuteProperty;
//                    }
//
//                    scrollMuteProperty.set(true);
//                    savedTimerRepository.create("latest", localTimeClock.getMainClock());
//                    return scrollMuteProperty;
//                })
//                .on(stopCountdown).transition((wasMuted, event) -> {
//                    scrollMuteProperty.set(false);
//                    return scrollMuteProperty;
//                })
//                .on(merged).emit((muted, t) -> muted.get() ? Optional.empty() : Optional.of(t))
//                .toEventStream().subscribe(event -> {
//                    if (((StackPane) event.getSource()).getId().contains("Seconds")) {
//                        this.deltaStreamSeconds.onNext((int)event.getDeltaY());
//                        timerColumns.get(ColumnType.SECONDS).play();
//                    }
//
//                    if (((StackPane) event.getSource()).getId().contains("Minutes")) {
//                        this.deltaStreamMinutes.onNext((int)event.getDeltaY());
//                        timerColumns.get(ColumnType.MINUTES).play();
//                    }
//                });

//        this.subscribe = ticks.subscribe(nullEvent -> {
//            this.deltaStreamSeconds.push(-60);
//            secondsColumn.play();
//        });
//        startClicks.subscribe(click -> {
//            if (scrollMuteProperty.get()) {
//                savedTimerRepository.create("latest", localTimeClock.getMainClock());
//            }
//        });

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
                .map(ignored -> new ClickEvent(ButtonType.START, ButtonState.valueOf(startButton.getText().toUpperCase())));

        Observable<ClickEvent> resetClickEvent = JavaFxObservable.eventsOf(resetButton, MouseEvent.MOUSE_CLICKED)
                .observeOn(Schedulers.computation())
                .map(ignored -> new ClickEvent(ButtonType.RESET, ButtonState.valueOf(resetButton.getText().toUpperCase())));

        Observable<ScrollEvent> scrollEvent = JavaFxObservable.eventsOf(seconds, javafx.scene.input.ScrollEvent.SCROLL)
                .observeOn(Schedulers.computation())
                .map(scrollMouseEvent -> new com.robotzero.counter.event.ScrollEvent(ColumnType.SECONDS, scrollMouseEvent.getDeltaY()));

        Observable<TickEvent> tickEvent = timerService.getTimer().map(ignored -> new TickEvent());

        Observable<MainViewEvent> mainViewEvents = Observable.merge(startClickEvent, resetClickEvent, scrollEvent, tickEvent);

        ObservableTransformer<ClickEvent, com.robotzero.counter.event.action.Action> clickEventTransformer = clickEvent -> clickEvent.flatMap(
          event -> {
              return Observable.just(new ClickAction(
                                ActionType.valueOf(event.getButtonType().descripton().toUpperCase()),
                                event.getButtonState()
                        )
              );
          });

        ObservableTransformer<ScrollEvent, com.robotzero.counter.event.action.Action> scrollEventTransformer = scrollMouseEvent -> scrollMouseEvent.flatMap(
                event -> {
                    return Observable.just(new ScrollAction(directionService.calculateDirection(event.getDelta()), event.getColumnType()));
                });

        ObservableTransformer<TickEvent, TickAction> tickEventTransformer = tickEv -> tickEv.flatMap(event -> {
            return Observable.just(new TickAction(Direction.DOWN));
        });

        ObservableTransformer<MainViewEvent, com.robotzero.counter.event.action.Action> actionTransformer = mainViewEvent -> mainViewEvent.publish(sharedObservable -> {
            return Observable.merge(
              sharedObservable.ofType(ClickEvent.class).compose(clickEventTransformer),
              sharedObservable.ofType(ScrollEvent.class).compose(scrollEventTransformer),
              sharedObservable.ofType(TickEvent.class).compose(tickEventTransformer)
            );
        });

        ObservableTransformer<ScrollAction, ScrollResult> scrollActionTransformer = scrollAction -> scrollAction.flatMap(action -> {
            return Observable.just(action);
        }).map(response -> new ScrollResult());

        ObservableTransformer<ClickAction, ClickResult> clickActionTransformer = clickAction -> clickAction.flatMap(action -> {
            return timerService.operateTimer(action);
        }).map(response -> new ClickResult(response.getActionType(), response.getNewButtonState()));

        ObservableTransformer<TickAction, TickResult> tickActionTransformer = tickAction -> tickAction.flatMap(action -> {
            return timerColumns.get(ColumnType.SECONDS).getTopCellObservable().withLatestFrom(clockService.tick(action.getDirection()), (cell, label) -> {
                return new TickResult(cell, label);
            });
        });

        Observable<com.robotzero.counter.event.action.Action> actions = mainViewEvents.compose(actionTransformer);

        Observable<Result> results = actions.publish(action -> {
            return Observable.merge(
                    action.ofType(ClickAction.class).compose(clickActionTransformer),
                    action.ofType(ScrollAction.class).compose(scrollActionTransformer),
                    action.ofType(TickAction.class).compose(tickActionTransformer)
            );
        });

        Observable<CurrentViewState> uiModels = results.scan(CurrentViewState.idle(), (startingState, intermediateState) -> {
            if (intermediateState.getClass().equals(ClickResult.class)) {
                ClickResult clickResult = (ClickResult) intermediateState;
                if (clickResult.getActionType().equals(ActionType.START)) {
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
            }

            if (intermediateState.getClass().equals(TickResult.class)) {
                TickResult tickResult = (TickResult) intermediateState;
                return CurrentViewState.tick(new CurrentViewData(null, null, tickResult));
            }

            return CurrentViewState.idle();
        });

        uiModels.observeOn(JavaFxScheduler.platform()).subscribe(currentViewState -> {
            if (currentViewState.isStart()) {
                startButton.textProperty().setValue(currentViewState.getData().getClickResult().getButtonState().getDescription());
            }

            if (currentViewState.isPause()) {
                startButton.textProperty().setValue(currentViewState.getData().getClickResult().getButtonState().getDescription());
            }

            if (currentViewState.isStop()) {
                startButton.textProperty().setValue(currentViewState.getData().getClickResult().getButtonState().getDescription());
            }

            if (currentViewState.isTick()) {
                Cell cell = currentViewState.getData().getTickResult().getCell();
                cell.setLabel(currentViewState.getData().getTickResult().getLabel());
                timerColumns.get(ColumnType.SECONDS).play(Direction.DOWN);
            }
        }, error -> {
            System.out.println(error.getMessage());
            throw new IOException("CRASHING");
        });

//        Observable<Long> ticksReact = timerService.getTimer();

//        ticksReact.flatMap(ignored -> {
//            return timerColumns.get(ColumnType.SECONDS).getTopCellObservable().withLatestFrom(clockService.tick(Direction.DOWN), (cell, label) -> {
//               return new TickAction(cell, label);
//            });
//        }).subscribe(notignored -> {
//            notignored.getCell().setLabel(notignored.getLabel());
//            timerColumns.get(ColumnType.SECONDS).play(Direction.DOWN);
//        });
    }
}
