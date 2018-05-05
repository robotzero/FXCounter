package com.robotzero.counter.clock;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.event.*;
import com.robotzero.counter.event.action.ActionType;
import com.robotzero.counter.event.action.ClickAction;
import com.robotzero.counter.event.action.TickAction;
import com.robotzero.counter.event.result.*;
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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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

    // Temp options simulating options screen
    @Autowired
    private BooleanProperty fetchFromDatabase;

    @Autowired
    private StageController stageController;

    @Autowired
    private DirectionService directionService;

    @Autowired
    private ClockService clockService;

    private Map<ColumnType, Column> timerColumns;

    private BooleanProperty timerMute = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        startButton.textProperty().bind(new When(scrollMuteProperty.isEqualTo(new SimpleBooleanProperty(false))).then("Start").otherwise("Pause"));
        timerMute.bind(startButton.armedProperty());
        this.timerColumns = this.populator.timerColumns(this.gridPane);
        Map<ColumnType, List<Integer>> initialValues = this.clockService.initialize(Direction.DOWN);
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
                .map(ignored -> new ClickEvent(ButtonType.START, ButtonState.valueOf(startButton.getText().toUpperCase())));

        Observable<ClickEvent> resetClickEvent = JavaFxObservable.eventsOf(resetButton, MouseEvent.MOUSE_CLICKED)
                .observeOn(Schedulers.computation())
                .map(ignored -> new ClickEvent(ButtonType.RESET, ButtonState.valueOf(resetButton.getText().toUpperCase())));

        Observable<ScrollEvent> scrollEvent = JavaFxObservable.eventsOf(seconds, javafx.scene.input.ScrollEvent.SCROLL)
                .mergeWith(JavaFxObservable.eventsOf(minutes, javafx.scene.input.ScrollEvent.SCROLL))
                .mergeWith(JavaFxObservable.eventsOf(hours, javafx.scene.input.ScrollEvent.SCROLL))
                .observeOn(Schedulers.computation())
                .debounce(700, TimeUnit.MILLISECONDS)
                .map(a -> {
                    System.out.println("SCROLL");
                    System.out.println(a.toString());
                    return a;
                })
                .map(scrollMouseEvent -> new com.robotzero.counter.event.ScrollEvent(((Node) scrollMouseEvent.getSource()).getId(), scrollMouseEvent.getDeltaY()));

        Observable<TickEvent> tickEvent = timerService.getTimer().map(elapsedTime -> new TickEvent(elapsedTime));

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
                    return Observable.just(new TickAction(directionService.calculateDirection(event.getDelta()), event.getColumnType(), TimerType.SCROLL));
                });

        ObservableTransformer<TickEvent, TickAction> tickEventTransformer = tickEv -> tickEv.flatMap(event -> {
            return Observable.just(new TickAction(Observable.just(Direction.DOWN), ColumnType.SECONDS, TimerType.TICK));
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

        ObservableTransformer<TickAction, TickResult> tickActionTransformer = tickAction -> tickAction.flatMap(action -> {
            return Observable.zip(
                    timerColumns.get(ColumnType.SECONDS).getTopCellObservable(),
                    timerColumns.get(ColumnType.MINUTES).getTopCellObservable(),
                    timerColumns.get(ColumnType.HOURS).getTopCellObservable(),
                    action.getDirection(),
                    (secondsCell, minutesCell, hoursCell, direction) -> {
                return new TickResult(secondsCell, minutesCell, hoursCell, direction, action.getColumnType(), action.getTimerType());
            }).withLatestFrom(action.getDirection().flatMap(direction -> clockService.tick(direction, action.getTimerType())), (tickResult, currentClockState) -> {
                return tickResult.withCurrentClockState(currentClockState);
            });
        });

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

                if (clickResult.getButtonState().equals(ButtonState.RESET)) {
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
            ClickResult clickResult = currentViewState.getData().getClickResult();
            if (currentViewState.isStart()) {
                startButton.textProperty().setValue(clickResult.getButtonState().getDescription());
            }

            if (currentViewState.isPause()) {
                startButton.textProperty().setValue(clickResult.getButtonState().getDescription());
            }

            if (currentViewState.isStop()) {
                startButton.textProperty().setValue(clickResult.getButtonState().getDescription());
            }

            if (currentViewState.isReset()) {
                startButton.textProperty().setValue("Start");
            }

            if (currentViewState.isTick()) {
                TickResult tickResult = currentViewState.getData().getTickResult();
                if (currentViewState.getData().getTickResult().getColumnType().equals(ColumnType.SECONDS)) {
                    Cell secondsCell = tickResult.getSecondsCell();
                    secondsCell.setLabel(tickResult.getLabels().getSecond());
                    timerColumns.get(secondsCell.getColumnType()).play(tickResult.getDirection());
                }

                if (tickResult.getLabels().shouldTickMinute() || tickResult.getColumnType().equals(ColumnType.MINUTES)) {
                    Cell minutesCell = tickResult.getMinutesCell();
                    minutesCell.setLabel(tickResult.getLabels().getMinute());
                    timerColumns.get(minutesCell.getColumnType()).play(tickResult.getDirection());
                }

                if (tickResult.getLabels().shouldTickHour() || tickResult.getColumnType().equals(ColumnType.HOURS)) {
                    Cell hoursCell = tickResult.getHoursCell();
                    hoursCell.setLabel(tickResult.getLabels().getHour());
                    timerColumns.get(hoursCell.getColumnType()).play(tickResult.getDirection());
                }
            }
        }, error -> {
            System.out.println(error.getMessage());
            throw new IOException("CRASHING THE APP...");
        });
    }
}
