package com.queen.counter.clock;

import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.Column;
import com.queen.counter.domain.SavedTimer;
import com.queen.counter.repository.SavedTimerRepository;
import com.queen.counter.service.Populator;
import com.queen.counter.service.StageController;
import javafx.beans.binding.When;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.reactfx.*;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Inject;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClockPresenter implements Initializable {

    @FXML
    GridPane gridPane;

    @FXML
    Button start, stop, reset;

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

    @Inject
    private Populator populator;

    @Inject
    private Clocks clocks;

    @Inject
    private SavedTimerRepository savedTimerRepository;

    // Temp options simulating options screen
    @Inject
    private BooleanProperty fetchFromDatabase;

    @Inject
    @Qualifier("PlayMinutes")
    private EventSource<Void> playMinutes;

    @Inject
    @Qualifier("PlayHours")
    private EventSource<Void> playHours;

    @Inject
    @Qualifier("StopCountdown")
    private EventSource<Void> stopCountdown;

    @Inject
    @Qualifier("DeltaStreamSeconds")
    private EventSource<Integer> deltaStreamSeconds;

    @Inject
    @Qualifier("DeltaStreamMinutes")
    private EventSource<Integer> deltaStreamMinutes;

    @Inject
    @Qualifier("DeltaStreamHours")
    private EventSource<Integer> deltaStreamHours;

    @Inject
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

        GridPane.setMargin(start, new Insets(20, 20, 20, 20));

        //@TODO merge them and then split/fork?
        EventStream<MouseEvent> startClicks = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED);
        EventStream<MouseEvent> stopClicks = EventStreams.eventsOf(stop, MouseEvent.MOUSE_CLICKED);
        EventStream<MouseEvent> resetClicks = EventStreams.eventsOf(reset, MouseEvent.MOUSE_CLICKED);
        EventStream<MouseEvent> optionClicks = EventStreams.eventsOf(optionsLabel, MouseEvent.MOUSE_CLICKED);

        EventStream<?> ticks = EventStreams.ticks(Duration.ofMillis(1000)).suppressWhen(scrollMuteProperty.not());
        Subscription.multi(playMinutes.conditionOn(scrollMuteProperty).thenIgnoreFor(Duration.ofMillis(700)).subscribe(v -> {
            this.deltaStreamMinutes.push(-60);
            minutesColumn.play();
        }), playHours.conditionOn(scrollMuteProperty).thenIgnoreFor(Duration.ofMillis(700)).subscribe(v -> {
            this.deltaStreamHours.push(-60);
            hoursColumn.play();
        }));

//        start.disableProperty().bind(scrollMuteProperty);
        stop.disableProperty().bind(scrollMuteProperty.not());
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
//                .on(stopClicks).transition((wasMuted, event) -> {
//                    scrollMuteProperty.set(false);
//                    return scrollMuteProperty;
//                })
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

        this.subscribe = ticks.subscribe(nullEvent -> {
            this.deltaStreamSeconds.push(-60);
            secondsColumn.play();
        });

        startClicks.subscribe(click -> {
            if (scrollMuteProperty.get()) {
                savedTimerRepository.create("latest", clocks.getMainClock());
            }
        });

//        stopClicks.subscribe(click -> {
////            this.subscribe.unsubscribe();
//            playM.unsubscribe();
//            playH.unsubscribe();
//        });

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
