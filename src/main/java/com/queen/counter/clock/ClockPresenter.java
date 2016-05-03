package com.queen.counter.clock;

import com.queen.animator.Animator;
import com.queen.configuration.SceneConfiguration;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.domain.Clocks;
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
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class ClockPresenter implements Initializable {

    @FXML
    GridPane gridPane;

    @FXML
    Pane seconds;

    @FXML
    Button start, stop;

    @FXML
    Group group;

    @FXML
    Group minutesgroup;

    @FXML
    Pane minutes;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.clocks.initializeClocks(LocalTime.of(0, 16, 12));
        this.uiService.setGroups(() -> Stream.of(group, minutesgroup));
        populator.populate();

        seconds.setStyle("-fx-background-color: #FFFFFF;");
        minutes.setStyle("-fx-background-color: #FFFFFF;");

        Binding b = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED).toBinding(null);

        EventStream<MouseEvent> buttonClicks = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED).suppressWhen(animator.isTicking());
        EventStream<MouseEvent>stopClicks = EventStreams.eventsOf(stop, MouseEvent.MOUSE_CLICKED).suppressWhen(animator.isTicking().not());
        EventStream<?> ticks = EventStreams.ticks(Duration.ofMillis(1000));

        EventStream<ScrollEvent> merged = EventStreams.merge(
                EventStreams.eventsOf(group, ScrollEvent.SCROLL).suppressWhen(animator.isRunning().or(animator.isTicking())),
                EventStreams.eventsOf(minutesgroup, ScrollEvent.SCROLL).suppressWhen(animator.isMinutesRunning())
        );

        merged.subscribe(event -> {
            String id = ((Group) event.getSource()).getId();
            src.set(id);
            scroller.scroll(id, stringBinding.getValue().toString(), event.getDeltaY());
        });

        buttonClicks.subscribe(click -> {
            animator.setRunning(true);
            animator.setMinutesRunning(true);
            animator.setTicking(true);
            scroller.scroll("group", "seconds", -40);
            this.subscribe  = ticks.subscribe((something) -> {
                    animator.setRunning(true);
                    animator.setMinutesRunning(true);
                    animator.setTicking(true);

                    this.scroller.scroll("group", "seconds", -40);
                    if (clocks.getScrollSecondsClock().minusSeconds(1).getSecond() == 59) {
                        this.scroller.scroll("minutesgroup", "minutes", -40);
                    }
                }
            );
        });

        stopClicks.subscribe(click -> {
            this.animator.setMinutesRunning(false);
            this.animator.setRunning(false);
            this.animator.setTicking(false);
            this.subscribe.unsubscribe();
        });
    }
}
