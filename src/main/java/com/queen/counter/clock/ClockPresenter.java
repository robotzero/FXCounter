package com.queen.counter.clock;

import com.queen.animator.Animator;
import com.queen.configuration.SceneConfiguration;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.domain.Clocks;
import com.queen.counter.service.Populator;
import com.queen.counter.service.Scroller;
import javafx.beans.binding.Binding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;

import javax.inject.Inject;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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

    private Subscription subscribe;

    private Supplier<Stream<Node>> rectanglesSupplier;

    private List<Text> labels;
    private List<Text> minuteslabels;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.clocks.initializeClocks(LocalTime.of(0, 16, 12));

        populator.populate(this.clocks.getMainClock(), Stream.of(group, minutesgroup));

        this.rectanglesSupplier =
                () -> Stream.of(group.getChildren(), minutesgroup.getChildren())
                            .flatMap(Collection::stream)
                            .filter(r -> r.getClass().equals(Rectangle.class));

        this.labels = group.getChildren().stream().filter(t -> t.getClass().equals(Text.class)).map(m -> (Text) m).collect(Collectors.toList());
        this.minuteslabels = minutesgroup.getChildren().stream().filter(n -> n.getClass().equals(Text.class)).map(m -> (Text) m).collect(Collectors.toList());

        seconds.setStyle("-fx-background-color: #FFFFFF;");
        minutes.setStyle("-fx-background-color: #FFFFFF;");

        Binding b = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED).toBinding(null);

        EventStream<MouseEvent> buttonClicks = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED).suppressWhen(animator.isTicking());
        EventStream<MouseEvent>stopClicks = EventStreams.eventsOf(stop, MouseEvent.MOUSE_CLICKED).suppressWhen(animator.isTicking().not());
        EventStream<ScrollEvent> minutesscroll = EventStreams.eventsOf(minutesgroup, ScrollEvent.SCROLL).suppressWhen(animator.isMinutesRunning());
        EventStream<?> ticks = EventStreams.ticks(Duration.ofMillis(1000));
        EventStream<ScrollEvent> scroll = EventStreams.eventsOf(group, ScrollEvent.SCROLL).suppressWhen(animator.isRunning().or(animator.isTicking()));

        scroll.map(ScrollEvent::getDeltaY).addObserver((delta) -> scroller.scroll(this.rectanglesSupplier.get().filter(r -> r.getId().contains("seconds")).collect(Collectors.toList()), this.labels, delta));
        minutesscroll.map(ScrollEvent::getDeltaY).addObserver((delta -> scroller.scroll(this.rectanglesSupplier.get().filter(r -> r.getId().contains("minutes")).collect(Collectors.toList()), this.minuteslabels, delta)));

        buttonClicks.subscribe(click -> {
            animator.setRunning(true);
            animator.setMinutesRunning(true);
            animator.setTicking(true);
            scroller.scroll(this.rectanglesSupplier.get().filter(r -> r.getId().contains("seconds")).collect(Collectors.toList()), this.labels, -40);
            this.subscribe  = ticks.subscribe((something) -> {
                    animator.setRunning(true);
                    animator.setMinutesRunning(true);
                    animator.setTicking(true);

                    this.scroller.scroll(this.rectanglesSupplier.get().filter(r -> r.getId().contains("seconds")).collect(Collectors.toList()), this.labels, -40);
                    if (clocks.getScrollSecondsClock().minusSeconds(1).getSecond() == 59) {
                        this.scroller.scroll(this.rectanglesSupplier.get().filter(r -> r.getId().contains("minutes")).collect(Collectors.toList()), this.minuteslabels, -40);
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
