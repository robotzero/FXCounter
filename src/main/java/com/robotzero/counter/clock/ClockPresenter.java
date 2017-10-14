package com.robotzero.counter.clock;

import com.robotzero.counter.domain.Clocks;
import com.robotzero.counter.domain.Column;
import com.robotzero.counter.domain.SavedTimer;
import com.robotzero.counter.repository.SavedTimerRepository;
import com.robotzero.counter.service.Populator;
import com.robotzero.counter.service.StageController;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import javafx.beans.binding.When;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.reactfx.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class ClockPresenter implements Initializable {

    @FXML
    GridPane gridPane;

    @FXML
    Button start, reset;

    @FXML
    StackPane paneSeconds;

    @FXML
    StackPane paneMinutes;

    @FXML
    StackPane paneHours;

    @FXML
    Text textSeconds;

    @FXML
    Text textMinutes;

    @FXML
    Text textHours;

    @FXML
    Label optionsLabel;

    @Autowired
    private Populator populator;

    @Autowired
    private Clocks clocks;

    @Autowired
    private SavedTimerRepository savedTimerRepository;

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
    private EventSource<Integer> deltaStreamSeconds;

    @Autowired
    @Qualifier("DeltaStreamMinutes")
    private EventSource<Integer> deltaStreamMinutes;

    @Autowired
    @Qualifier("DeltaStreamHours")
    private EventSource<Integer> deltaStreamHours;

    @Autowired
    private StageController stageController;

    private BooleanProperty scrollMuteProperty = new SimpleBooleanProperty(false);

    private Subscription subscribe;

    private Column secondsColumn;
    private Column minutesColumn;
    private Column hoursColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.clocks.initializeClocks(Optional.ofNullable(savedTimerRepository.selectLatest()).orElseGet(() -> {
            SavedTimer savedTimer = new SavedTimer();
            savedTimer.setSavedTimer(LocalTime.of(0, 0, 0));
            return savedTimer;
        }).getSavedTimer());

        start.textProperty().bind(new When(scrollMuteProperty.isEqualTo(new SimpleBooleanProperty(false))).then("Start").otherwise("Pause"));
        secondsColumn = populator.create(paneSeconds);
        minutesColumn = populator.create(paneMinutes);
        hoursColumn = populator.create(paneHours);

        //@TODO merge them and then split/fork?
        EventStream<MouseEvent> startClicks = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED);
        EventStream<MouseEvent> resetClicks = EventStreams.eventsOf(reset, MouseEvent.MOUSE_CLICKED);
        EventStream<MouseEvent> optionClicks = EventStreams.eventsOf(optionsLabel, MouseEvent.MOUSE_CLICKED);

        Flowable<Long> ticksReact =  Flowable.interval(1, TimeUnit.SECONDS).skipWhile(time -> {
            return scrollMuteProperty.not().get();
        });
        EventStream<?> ticks = EventStreams.ticks(Duration.ofMillis(1000)).suppressWhen(scrollMuteProperty.not());
        Subscription.multi(playMinutes.conditionOn(scrollMuteProperty).thenIgnoreFor(Duration.ofMillis(700)).subscribe(v -> {
            this.deltaStreamMinutes.push(-60);
            minutesColumn.play();
        }), playHours.conditionOn(scrollMuteProperty).thenIgnoreFor(Duration.ofMillis(700)).subscribe(v -> {
            this.deltaStreamHours.push(-60);
            hoursColumn.play();
        }));

        reset.disableProperty().bind(scrollMuteProperty);
        EventStream<ScrollEvent> merged = EventStreams.merge(
                EventStreams.eventsOf(paneSeconds, ScrollEvent.SCROLL).suppressWhen(secondsColumn.isRunning()),
                EventStreams.eventsOf(paneMinutes, ScrollEvent.SCROLL).suppressWhen(minutesColumn.isRunning()),
                EventStreams.eventsOf(paneHours, ScrollEvent.SCROLL).suppressWhen(hoursColumn.isRunning())
        );

        stopCountdown.subscribe(v -> {
            System.out.println("BEEP");
        });

        // If start button is clicked mute scroll event until stop button is clicked.
        StateMachine.init(scrollMuteProperty)
                .on(startClicks).transition((wasMuted, event) -> {
                    if (wasMuted.get()) {
                        scrollMuteProperty.set(false);
                        return scrollMuteProperty;
                    }

                    if (clocks.getMainClock().compareTo(LocalTime.of(0, 0, 0)) == 0) {
                        return scrollMuteProperty;
                    }

                    scrollMuteProperty.set(true);
                    savedTimerRepository.create("latest", clocks.getMainClock());
                    return scrollMuteProperty;
                })
                .on(stopCountdown).transition((wasMuted, event) -> {
                    scrollMuteProperty.set(false);
                    return scrollMuteProperty;
                })
                .on(merged).emit((muted, t) -> muted.get() ? Optional.empty() : Optional.of(t))
                .toEventStream().subscribe(event -> {
                    if (((StackPane) event.getSource()).getId().contains("Seconds")) {
                        this.deltaStreamSeconds.push((int)event.getDeltaY());
                        secondsColumn.play();
                    }

                    if (((StackPane) event.getSource()).getId().contains("Minutes")) {
                        this.deltaStreamMinutes.push((int)event.getDeltaY());
                        minutesColumn.play();
                    }
                });

//        this.subscribe = ticks.subscribe(nullEvent -> {
//            this.deltaStreamSeconds.push(-60);
//            secondsColumn.play();
//        });

        ticksReact.subscribe(
            v -> {this.deltaStreamSeconds.push(-60); this.secondsColumn.play();},
            e -> System.out.println("Error: " + e),
            () -> System.out.println("Completed")
        );

        startClicks.subscribe(click -> {
            if (scrollMuteProperty.get()) {
                savedTimerRepository.create("latest", clocks.getMainClock());
            }
        });

        optionClicks.subscribe(click -> stageController.setView());

        resetClicks.subscribe(click -> {
            this.fetchFromDatabase.setValue(true);
            if (fetchFromDatabase.get()) {
                this.clocks.initializeClocks(Optional.ofNullable(savedTimerRepository.selectLatest()).orElseGet(() -> {
                    SavedTimer savedTimer = new SavedTimer();
                    savedTimer.setSavedTimer(LocalTime.of(0, 0, 0));
                    return savedTimer;
                }).getSavedTimer());
            } else {
                this.clocks.initializeClocks(LocalTime.of(0, 0, 0));
            }
            this.deltaStreamSeconds.push(0);
            this.deltaStreamMinutes.push(0);
            this.deltaStreamHours.push(0);
            secondsColumn.setLabels();
            minutesColumn.setLabels();
            hoursColumn.setLabels();
        });
    }
}
