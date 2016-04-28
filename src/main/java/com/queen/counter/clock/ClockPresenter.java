package com.queen.counter.clock;

import com.queen.animator.Animator;
import com.queen.configuration.SceneConfiguration;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.domain.AnimationMetadata;
import com.queen.counter.domain.Clocks;
import com.queen.counter.service.Populator;
import com.queen.counter.service.Scroller;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClockPresenter implements Initializable {

    public final static int cellsize = 60;
    public final static int blockCount = 4;

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

    private List<Node> rectangles;
    private List<Node> minutesRectangles;

    public static LocalTime userTime;

    private List<Text> labels;
    private List<Text> minuteslabels;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        userTime = LocalTime.of(0, 16, 12);
        this.clocks.initializeClocks(LocalTime.of(0, 16, 12));

        populator.populateSeconds(userTime, group, "seconds");
        populator.populateSeconds(userTime, minutesgroup, "minutes");

        this.rectangles = group.getChildren().stream().filter(n -> n.getClass().equals(Rectangle.class)).collect(Collectors.toList());
        this.minutesRectangles = minutesgroup.getChildren().stream().filter(n -> n.getClass().equals(Rectangle.class)).collect(Collectors.toList());
        this.labels = group.getChildren().stream().filter(t -> t.getClass().equals(Text.class)).map(m -> (Text) m).collect(Collectors.toList());
        this.minuteslabels = minutesgroup.getChildren().stream().filter(n -> n.getClass().equals(Text.class)).map(m -> (Text) m).collect(Collectors.toList());

        seconds.setStyle("-fx-background-color: #FFFFFF;");
        minutes.setStyle("-fx-background-color: #FFFFFF;");

        EventStream<MouseEvent> buttonClicks = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED).suppressWhen(animator.isTicking());
        EventStream<MouseEvent>stopClicks = EventStreams.eventsOf(stop, MouseEvent.MOUSE_CLICKED).suppressWhen(animator.isTicking().not());
        EventStream<ScrollEvent> minutesscroll = EventStreams.eventsOf(minutesgroup, ScrollEvent.SCROLL).suppressWhen(animator.isMinutesRunning());
        EventStream<?> ticks = EventStreams.ticks(Duration.ofMillis(1000));
        EventStream<ScrollEvent> scroll = EventStreams.eventsOf(group, ScrollEvent.SCROLL).suppressWhen(animator.isRunning().or(animator.isTicking()));

        scroll.subscribe(scrollEvent -> {
            scroller.scroll(this.rectangles, this.labels, scrollEvent.getDeltaY());
        });

        minutesscroll.subscribe(scrollEvent -> {
            scroller.scroll(this.minutesRectangles, this.minuteslabels, scrollEvent.getDeltaY());
        });

        buttonClicks.subscribe(click -> {
            animator.setRunning(true);
            animator.setMinutesRunning(true);
            animator.setTicking(true);
            scroller.scroll(this.rectangles, this.labels, -40);
            this.subscribe  = ticks.subscribe((something) -> {
                    animator.setRunning(true);
                    animator.setMinutesRunning(true);
                    animator.setTicking(true);

                    this.scroller.scroll(this.rectangles, this.labels, -40);
                    if (clocks.getScrollSecondsClock().minusSeconds(1).getSecond() == 59) {
                        this.scroller.scroll(this.minutesRectangles, this.minuteslabels, -40);
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
