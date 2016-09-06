package com.queen.counter.clock;

import com.queen.configuration.SceneConfiguration;
import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.Column;
import com.queen.counter.domain.ColumnType;
import com.queen.counter.domain.SavedTimer;
import com.queen.counter.repository.SavedTimerRepository;
import com.queen.counter.service.Populator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.reactfx.*;
import org.reactfx.util.Tuple2;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Inject;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.reactfx.util.Tuples.t;

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

    @Inject
    private SceneConfiguration sceneConfiguration;

    @Inject
    private Populator populator;

    @Inject
    private Clocks clocks;

    @Inject
    private SavedTimerRepository savedTimerRepository;

    // Temp option simulating options screen
    @Inject
    private BooleanProperty fetchFromDatabase;

    @Inject
    @Qualifier("PlayMinutes")
    private EventSource<Void> playMinutes;

    @Inject
    @Qualifier("PlayHours")
    private EventSource<Void> playHours;

    @Inject
    @Qualifier("DeltaStream")
    private EventSource<Tuple2<Integer, ColumnType>> deltaStream;

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


        secondsColumn = populator.create(paneSeconds);
        minutesColumn = populator.create(paneMinutes);
        hoursColumn = populator.create(paneHours);

//        GridPane.setMargin(textSeconds, new Insets(320, 0, 320, 0));
//        GridPane.setMargin(textMinutes, new Insets(320, 0, 320, 0));
//        GridPane.setMargin(textHours, new Insets(320, 0, 320, 0));

        secondsColumn.setLabels();
        minutesColumn.setLabels();
        hoursColumn.setLabels();

        //@TODO merge them and then split/fork?
        EventStream<MouseEvent> startClicks = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED);
        EventStream<MouseEvent> stopClicks = EventStreams.eventsOf(stop, MouseEvent.MOUSE_CLICKED);
        EventStream<MouseEvent> resetClicks = EventStreams.eventsOf(reset, MouseEvent.MOUSE_CLICKED);
        EventStream<?> ticks = EventStreams.ticks(Duration.ofMillis(1000)).suppressWhen(scrollMuteProperty.not());
        Subscription playM = playMinutes.conditionOn(scrollMuteProperty).thenIgnoreFor(Duration.ofMillis(700)).subscribe(v -> {
            this.deltaStream.push(t(-60, ColumnType.MINUTES));
            minutesColumn.play();
        });

        Subscription playH = playHours.emitOn(ticks).emitOn(startClicks).onRecurseRetainLatest().subscribe(v -> {

        });

        start.disableProperty().bind(scrollMuteProperty);
        stop.disableProperty().bind(scrollMuteProperty.not());
        reset.disableProperty().bind(scrollMuteProperty);
        EventStream<ScrollEvent> merged = EventStreams.merge(
                EventStreams.eventsOf(paneSeconds, ScrollEvent.SCROLL).suppressWhen(secondsColumn.isRunning()),
                EventStreams.eventsOf(paneMinutes, ScrollEvent.SCROLL).suppressWhen(minutesColumn.isRunning()),
                EventStreams.eventsOf(paneHours, ScrollEvent.SCROLL).suppressWhen(hoursColumn.isRunning())
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
                    if (((StackPane) event.getSource()).getId().contains("Seconds")) {
                        this.deltaStream.push(t((int)event.getDeltaY(), ColumnType.SECONDS));
                        secondsColumn.play();
                    }

                    if (((StackPane) event.getSource()).getId().contains("Minutes")) {
                        this.deltaStream.push(t((int)event.getDeltaY(), ColumnType.MINUTES));
                        minutesColumn.play();
                    }
                });

        this.subscribe = ticks.subscribe(nullEvent -> {
            this.deltaStream.push(t(-60, ColumnType.SECONDS));
            secondsColumn.play();
        });

        startClicks.subscribe(click -> savedTimerRepository.create("latest", clocks.getMainClock()));

        stopClicks.subscribe(click -> {
            this.subscribe.unsubscribe();
            playM.unsubscribe();
            playH.unsubscribe();
        });

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
            this.deltaStream.push(t(0, ColumnType.SECONDS));
            this.deltaStream.push(t(0, ColumnType.MINUTES));
            this.deltaStream.push(t(0, ColumnType.HOURS));
            secondsColumn.setLabels();
            minutesColumn.setLabels();
            hoursColumn.setLabels();
        });
    }
}
