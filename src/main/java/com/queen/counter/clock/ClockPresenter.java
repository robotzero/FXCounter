package com.queen.counter.clock;

import com.queen.animator.Animator;
import com.queen.configuration.SceneConfiguration;
import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.Column;
import com.queen.counter.domain.SavedTimer;
import com.queen.counter.repository.SavedTimerRepository;
import com.queen.counter.service.Populator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.reactfx.*;

import javax.inject.Inject;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class ClockPresenter implements Initializable {

    @FXML
    GridPane gridPane;

    @FXML
    Pane paneMinutes;

    @FXML
    Button start, stop, reset;

    @FXML
    Group seconds;

    @FXML
    Group minutes;

    @FXML
    Pane paneSeconds;

    @Inject
    private SceneConfiguration sceneConfiguration;

    @Inject
    private Animator animator;

    @Inject
    private Populator populator;

    @Inject
    private Clocks clocks;

    @Inject
    private SavedTimerRepository savedTimerRepository;

    // Temp option simulating options screen
    @Inject
    private BooleanProperty fetchFromDatabase;

    private BooleanProperty scrollMuteProperty = new SimpleBooleanProperty(false);

    private Subscription subscribe;

    private Column secondsColumn;
    private Column minutesColumn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.clocks.initializeClocks(Optional.ofNullable(savedTimerRepository.selectLatest()).orElseGet(() -> {
            SavedTimer savedTimer = new SavedTimer();
            savedTimer.setSavedTimer(LocalTime.of(0, 0, 0));
            return savedTimer;
        }).getSavedTimer());

        secondsColumn = populator.create(seconds.getId(), seconds);
        minutesColumn = populator.create(minutes.getId(), minutes);
        paneSeconds.setStyle("-fx-background-color: #FFFFFF;");
        paneMinutes.setStyle("-fx-background-color: #FFFFFF;");

        EventStream<MouseEvent> startClicks = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED);
        EventStream<MouseEvent> stopClicks = EventStreams.eventsOf(stop, MouseEvent.MOUSE_CLICKED);
        EventStream<MouseEvent> resetClicks = EventStreams.eventsOf(reset, MouseEvent.MOUSE_CLICKED);
        EventStream<?> ticks = EventStreams.ticks(Duration.ofMillis(1000));

        start.disableProperty().bind(scrollMuteProperty);
        stop.disableProperty().bind(scrollMuteProperty.not());
        reset.disableProperty().bind(scrollMuteProperty);
        EventStream<ScrollEvent> merged = EventStreams.merge(
                EventStreams.eventsOf(seconds, ScrollEvent.SCROLL).suppressWhen(secondsColumn.isRunning()),
                EventStreams.eventsOf(minutes, ScrollEvent.SCROLL).suppressWhen(minutesColumn.isRunning())
        );

        // If start button is clicked mute scroll event until stop button is clicked.
        StateMachine.init(scrollMuteProperty)
                .on(startClicks).transition((wasMuted, event) -> {
                    scrollMuteProperty.set(true);
                    return scrollMuteProperty;
                })
                .on(stopClicks).transition((wasMuted, event) -> {
                    scrollMuteProperty.set(false);
                    return scrollMuteProperty;
                })
                .on(merged).emit((muted, t) -> muted.get() ? Optional.empty() : Optional.of(t))
                .toEventStream().subscribe(event -> {
                    if (((Group) event.getSource()).getId().contains("seconds")) {
                        secondsColumn.shift(event.getDeltaY());
                        secondsColumn.play();
                    }

                    if (((Group) event.getSource()).getId().contains("minutes")) {
                        minutesColumn.shift(event.getDeltaY());
                        minutesColumn.play();
                    }
                });

        startClicks.subscribe(click -> {
            savedTimerRepository.create("latest", clocks.getMainClock());
            secondsColumn.shift(-60);
            secondsColumn.play();
            this.subscribe  = ticks.subscribe((nullEvent) -> {
                secondsColumn.shift(-60);
                secondsColumn.play();
                if (clocks.getScrollSecondsClock().minusSeconds(1).getSecond() == 59) {
                    minutesColumn.shift(-60);
                    minutesColumn.play();
                    }
                }
            );
        });

        stopClicks.subscribe(click -> this.subscribe.unsubscribe());

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
            secondsColumn.setLabels();
            minutesColumn.setLabels();
        });
    }
}
