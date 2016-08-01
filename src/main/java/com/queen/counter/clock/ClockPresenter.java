package com.queen.counter.clock;

import com.queen.animator.Animator;
import com.queen.configuration.SceneConfiguration;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.Column;
import com.queen.counter.domain.UIService;
import com.queen.counter.service.Populator;
import com.queen.counter.service.Scroller;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import java.util.stream.Stream;

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
    private Scroller scroller;

    @Inject
    private InMemoryCachedServiceLocator locator;

    @Inject
    private Clocks clocks;

    @Inject
    private UIService uiService;

    private Subscription subscribe;

    private StringProperty src = new SimpleStringProperty();
    private Binding stringBinding = Bindings.createStringBinding(() -> src.getValue().equals("group") ? "seconds" : "minutes", src);

    private Column secondsColumn;
    private Column minutesColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.clocks.initializeClocks(LocalTime.of(0, 16, 12));
        this.uiService.setGroups(() -> Stream.of(seconds, minutes));
        //populator.populate(seconds, minutes);
        secondsColumn = populator.create(seconds.getId(), seconds);
        minutesColumn = populator.create(minutes.getId(), minutes);
        paneSeconds.setStyle("-fx-background-color: #FFFFFF;");
        paneMinutes.setStyle("-fx-background-color: #FFFFFF;");

        Binding b = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED).toBinding(null);

        EventStream<MouseEvent> buttonClicks = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED).suppressWhen(animator.isTicking());
        EventStream<MouseEvent> stopClicks = EventStreams.eventsOf(stop, MouseEvent.MOUSE_CLICKED).suppressWhen(animator.isTicking().not());
        EventStream<?> ticks = EventStreams.ticks(Duration.ofMillis(1000));

        EventStream<ScrollEvent> merged = EventStreams.merge(
//                EventStreams.eventsOf(seconds, ScrollEvent.SCROLL).suppressWhen(animator.isRunning().or(animator.isTicking())),
                EventStreams.eventsOf(seconds, ScrollEvent.SCROLL).suppressWhen(secondsColumn.isRunning()),
                EventStreams.eventsOf(minutes, ScrollEvent.SCROLL).suppressWhen(minutesColumn.isRunning())
        );

        merged.subscribe(event -> {
//            animator.setRunning(true);
//            animator.setTicking(true);
            String id = ((Group) event.getSource()).getId();
            if (((Group) event.getSource()).getId().contains("seconds")) {
                secondsColumn.setRunning(true);
                secondsColumn.shift(event.getDeltaY(), "seconds");
                secondsColumn.play();
            }

            if (((Group) event.getSource()).getId().contains("minutes")) {
                minutesColumn.setRunning(true);
                minutesColumn.shift(event.getDeltaY(), "minutes");
                minutesColumn.play();
            }
            //scroller.scroll(id, event.getDeltaY());
        });

        buttonClicks.subscribe(click -> {
            animator.setRunning(true);
            animator.setMinutesRunning(true);
            animator.setTicking(true);
            secondsColumn.setRunning(true);
            minutesColumn.setRunning(true);
            secondsColumn.shift(-60, "seconds");
            secondsColumn.play();
            //scroller.scroll("group", -40);
            this.subscribe  = ticks.subscribe((something) -> {
                    animator.setRunning(true);
                    animator.setMinutesRunning(true);
                    animator.setTicking(true);
                    secondsColumn.shift(-60, "seconds");
                    secondsColumn.play();
                    //this.scroller.scroll("group", -40);
                    if (clocks.getScrollSecondsClock().minusSeconds(1).getSecond() == 59) {
                        //this.scroller.scroll("minutesgroup",  -40);
                        minutesColumn.shift(-60, "minutes");
                        minutesColumn.play();
                    }
                }
            );
        });

        stopClicks.subscribe(click -> {
            secondsColumn.setRunning(false);
            minutesColumn.setRunning(false);
            this.animator.setMinutesRunning(false);
            this.animator.setRunning(false);
            this.animator.setTicking(false);
            this.subscribe.unsubscribe();
        });
    }
}
