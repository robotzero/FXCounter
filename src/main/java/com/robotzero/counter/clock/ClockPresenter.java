package com.robotzero.counter.clock;

import com.robotzero.counter.domain.clock.Clocks;
import com.robotzero.counter.domain.Column;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.service.Populator;
import com.robotzero.counter.service.StageController;
import io.reactivex.Flowable;
import io.reactivex.subjects.Subject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import org.reactfx.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class ClockPresenter implements Initializable {

    @FXML
    GridPane gridPane;

//    @FXML
//    Button start, reset;

//    @FXML
//    StackPane paneSeconds;
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

    @Autowired
    private Clocks clocks;
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
    private Subject<Integer> deltaStreamSeconds;

    @Autowired
    @Qualifier("DeltaStreamMinutes")
    private Subject<Integer> deltaStreamMinutes;

    @Autowired
    @Qualifier("DeltaStreamHours")
    private Subject<Integer> deltaStreamHours;

    @Autowired
    private StageController stageController;

    private BooleanProperty scrollMuteProperty = new SimpleBooleanProperty(false);

    private Subscription subscribe;

    private Map<ColumnType, Column> timerColumns;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        start.textProperty().bind(new When(scrollMuteProperty.isEqualTo(new SimpleBooleanProperty(false))).then("Start").otherwise("Pause"));

        this.timerColumns = this.populator.timerColumns(this.gridPane, clocks);
        //@TODO merge them and then split/fork?
//        EventStream<MouseEvent> startClicks = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED);
//        EventStream<MouseEvent> resetClicks = EventStreams.eventsOf(reset, MouseEvent.MOUSE_CLICKED);
//        EventStream<MouseEvent> optionClicks = EventStreams.eventsOf(optionsLabel, MouseEvent.MOUSE_CLICKED);

//        Flowable<Long> ticksReact = Flowable.interval(1, TimeUnit.SECONDS).skipWhile(time -> {
//            return scrollMuteProperty.not().get();
//        });
        Flowable<Long> ticksReact = Flowable.interval(1, TimeUnit.SECONDS);
        EventStream<?> ticks = EventStreams.ticks(Duration.ofMillis(1000)).suppressWhen(scrollMuteProperty.not());
//        Subscription.multi(playMinutes.conditionOn(scrollMuteProperty).thenIgnoreFor(Duration.ofMillis(700)).subscribe(v -> {
//            this.deltaStreamMinutes.onNext(-60);
//            timerColumns.get(ColumnType.MINUTES).play();
//        }), playHours.conditionOn(scrollMuteProperty).thenIgnoreFor(Duration.ofMillis(700)).subscribe(v -> {
//            this.deltaStreamHours.onNext(-60);
//            timerColumns.get(ColumnType.HOURS).play();
//        }));

//        reset.disableProperty().bind(scrollMuteProperty);
//        EventStream<ScrollEvent> merged = EventStreams.merge(
//                EventStreams.eventsOf(paneSeconds, ScrollEvent.SCROLL).suppressWhen(timerColumns.get(ColumnType.SECONDS).isRunning()),
//                EventStreams.eventsOf(paneMinutes, ScrollEvent.SCROLL).suppressWhen(timerColumns.get(ColumnType.MINUTES).isRunning()),
//                EventStreams.eventsOf(paneHours, ScrollEvent.SCROLL).suppressWhen(timerColumns.get(ColumnType.HOURS).isRunning())
//        );

//        stopCountdown.subscribe(v -> {
//            System.out.println("BEEP");
//        });

        // If start button is clicked mute scroll event until stop button is clicked.
//        StateMachine.init(scrollMuteProperty)
//                .on(startClicks).transition((wasMuted, event) -> {
//                    if (wasMuted.get()) {
//                        scrollMuteProperty.set(false);
//                        return scrollMuteProperty;
//                    }
//
//                    if (clocks.getMainClock().compareTo(LocalTime.of(0, 0, 0)) == 0) {
//                        return scrollMuteProperty;
//                    }
//
//                    scrollMuteProperty.set(true);
//                    savedTimerRepository.create("latest", clocks.getMainClock());
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

        ticksReact.subscribe(
                (value) -> {
                    System.out.println(value);
                    this.deltaStreamSeconds.onNext(-1);
                    this.timerColumns.get(ColumnType.SECONDS).play();
                },
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed, Play sound.")
        );

//        startClicks.subscribe(click -> {
//            if (scrollMuteProperty.get()) {
//                savedTimerRepository.create("latest", clocks.getMainClock());
//            }
//        });

//        optionClicks.subscribe(click -> stageController.setView());

//        resetClicks.subscribe(click -> {
//            this.fetchFromDatabase.setValue(true);
//            if (fetchFromDatabase.get()) {
//                this.clocks.initializeClocks(Optional.ofNullable(savedTimerRepository.selectLatest()).orElseGet(() -> {
////                this.clocks.initializeClocks(Optional.ofNullable(savedTimerRepository.selectLatest()).orElseGet(() -> {
//                    Clock savedTimer = new Clock();
//                    savedTimer.setSavedTimer(LocalTime.of(0, 0, 0));
//                    return savedTimer;
//                }).getSavedTimer());
//            } else {
//                this.clocks.initializeClocks(LocalTime.of(0, 0, 0));
//            }
//            this.deltaStreamSeconds.onNext(0);
//            this.deltaStreamMinutes.onNext(0);
//            this.deltaStreamHours.onNext(0);
//            timerColumns.forEach((columnType, column) -> {
//                column.setLabels();
//            });
//            timerColumns.get(ColumnType.SECONDS).setLabels();
//        });
    }
}
