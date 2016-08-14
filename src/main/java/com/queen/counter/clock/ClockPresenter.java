package com.queen.counter.clock;

import com.queen.animator.Animator;
import com.queen.configuration.SceneConfiguration;
import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.Column;
import com.queen.counter.domain.SavedTimer;
import com.queen.counter.repository.SavedTimerRepository;
import com.queen.counter.service.Populator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;

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
    Button start, stop;

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

        EventStream<MouseEvent> startClicks = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED).suppressWhen(secondsColumn.isTicking().or(minutesColumn.isTicking()));
        EventStream<MouseEvent> stopClicks = EventStreams.eventsOf(stop, MouseEvent.MOUSE_CLICKED).suppressWhen(secondsColumn.isTicking().not().or(minutesColumn.isTicking().not()));
        EventStream<?> ticks = EventStreams.ticks(Duration.ofMillis(1000));

        EventStream<ScrollEvent> merged = EventStreams.merge(
                EventStreams.eventsOf(seconds, ScrollEvent.SCROLL).suppressWhen(secondsColumn.isRunning().or(secondsColumn.isTicking())),
                EventStreams.eventsOf(minutes, ScrollEvent.SCROLL).suppressWhen(minutesColumn.isRunning().or(minutesColumn.isTicking()))
        );

        merged.subscribe(event -> {
            if (((Group) event.getSource()).getId().contains("seconds")) {
                secondsColumn.setRunning(true);
                secondsColumn.shift(event.getDeltaY());
                secondsColumn.play();
            }

            if (((Group) event.getSource()).getId().contains("minutes")) {
                minutesColumn.setRunning(true);
                minutesColumn.shift(event.getDeltaY());
                minutesColumn.play();
            }
        });

        startClicks.subscribe(click -> {
            savedTimerRepository.create("latest", clocks.getMainClock());
            secondsColumn.setTicking(true);
            minutesColumn.setTicking(true);
            secondsColumn.shift(-60);
            secondsColumn.play();
            this.subscribe  = ticks.subscribe((something) -> {
                secondsColumn.shift(-60);
                secondsColumn.play();
                if (clocks.getScrollSecondsClock().minusSeconds(1).getSecond() == 59) {
                    minutesColumn.shift(-60);
                    minutesColumn.play();
                    }
                }
            );
        });

        stopClicks.subscribe(click -> {
            secondsColumn.setTicking(false);
            minutesColumn.setTicking(false);
            this.subscribe.unsubscribe();
        });
    }
}
