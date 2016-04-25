package com.queen.counter.clock;

import com.queen.animator.Animator;
import com.queen.configuration.SceneConfiguration;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.domain.AnimationMetadata;
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

    private Subscription subscribe;

    private List<Node> rectangles;
    private List<Node> minutesRectangles;

    private IntegerProperty clock = new SimpleIntegerProperty();
    public static LocalTime userTime;

    public static LocalTime time;

    private List<Text> labels;
    private List<Text> minuteslabels;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        userTime = LocalTime.of(0, 16, 12);
        this.scroller.setUserTimeSeconds(userTime.getSecond());
        this.scroller.setUserTimeMinutes(userTime.getMinute());

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
            userTime = userTime.minusSeconds(1);
            time = userTime.minusSeconds(1);
            clock.set(time.getSecond());

            this.rectangles.stream().filter(r -> r.getTranslateY() == 0).forEach(r -> {
                this.labels.stream().filter(lbl -> lbl.getId().equals(r.getId())).findFirst().ifPresent(l -> l.setText(clock.get() + ""));
            });

            List<AnimationMetadata> l1 = this.rectangles.stream().map(r -> (AnimationMetadata) locator.get(AnimationMetadata.class, r)).collect(Collectors.toList());
            animator.animate(l1, 0, locator);
            this.subscribe  = ticks.subscribe((something) -> {
                    animator.setRunning(true);
                    animator.setMinutesRunning(true);
                    animator.setTicking(true);
                    userTime = userTime.minusSeconds(1);
                    time = userTime.minusSeconds(1);
                    if (userTime.getSecond() == 0) {
                        List<AnimationMetadata> l = this.minutesRectangles.stream()
                                                        .map(r -> (AnimationMetadata) locator.get(AnimationMetadata.class, r))
                                                        .collect(Collectors.toList());

                        this.minutesRectangles.stream().filter(r -> r.getTranslateY() == 0).forEach(r -> {
                            this.minuteslabels.stream().filter(lbl -> lbl.getId().equals(r.getId())).findFirst().ifPresent(lbl -> lbl.setText(userTime.getMinute() -  2 + ""));
                        });
                        this.scroller.setUserTimeMinutes(userTime.minusMinutes(1).getMinute());
                        animator.animate(l, 0, locator);
                    }
                    clock.set(time.getSecond());

                    List<AnimationMetadata> l = this.rectangles.stream()
                        .map(r -> (AnimationMetadata) locator.get(AnimationMetadata.class, r))
                        .collect(Collectors.toList());

                    this.rectangles.stream().filter(r -> r.getTranslateY() == 0).forEach(r -> {
                        this.labels.stream().filter(lbl -> lbl.getId().equals(r.getId())).findFirst().ifPresent(lbl -> lbl.setText(clock.get() + ""));
                    });
                    animator.animate(l, 0, locator);
                }
            );

        });

        stopClicks.subscribe(click -> {
            this.scroller.setUserTimeSeconds(userTime.getSecond());
            this.scroller.setUserTimeMinutes(userTime.getMinute());
            this.animator.setMinutesRunning(false);
            this.animator.setRunning(false);
            this.animator.setTicking(false);
            this.subscribe.unsubscribe();
        });
    }
}
