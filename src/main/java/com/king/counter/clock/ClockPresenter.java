package com.king.counter.clock;

import com.googlecode.totallylazy.collections.PersistentMap;
import com.king.animator.Animator;
import com.king.configuration.SceneConfiguration;
import com.king.counter.domain.AnimationMetadata;
import com.sun.org.apache.regexp.internal.RE;
import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;

import javax.inject.Inject;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.googlecode.totallylazy.collections.PersistentMap.constructors.map;

public class ClockPresenter implements Initializable {

    private final static int cellsize = 60;
    private final static int blockCount = 4;

    @FXML
    GridPane gridPane;

    @FXML
    Pane seconds;

    @FXML
    Button start, stop;

    @FXML
    Group group;

    private final List<AnimationMetadata> animationMetadatas = new ArrayList<>();

    @Inject
    private SceneConfiguration sceneConfiguration;

    @Inject
    private Animator animator;

    private Subscription subscribe;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.populateSeconds();

        EventStream<MouseEvent>buttonClicks = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED);
        EventStream<MouseEvent>stopClicks = EventStreams.eventsOf(stop, MouseEvent.MOUSE_CLICKED);
        EventStream<?> ticks = EventStreams.ticks(Duration.ofMillis(1000));

        buttonClicks.subscribe(click -> {
            animator.animate(this.animationMetadatas);
            this.subscribe  = ticks.subscribe((something) -> {
                    List<AnimationMetadata> l = this.group.getChildren().stream().map(n -> new AnimationMetadata((Rectangle) n)).collect(Collectors.toList());
                    animator.animate(l);
                }
            );
        });

        stopClicks.subscribe(click -> {
            System.out.println("UNSUBSCRIBED");
            this.subscribe.unsubscribe();
        });
    }

    private void populateSeconds() {
        Random random = new Random();

        IntStream.range(0, blockCount).mapToObj(i -> {
            Rectangle rectangle = new Rectangle(cellsize, 0, cellsize, cellsize);
            rectangle.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 1));
            rectangle.setStrokeType(StrokeType.INSIDE);
            rectangle.setStroke(Color.BLACK);
            rectangle.setTranslateY(cellsize * i);

            AnimationMetadata animationMetadata = new AnimationMetadata(rectangle);
            animationMetadatas.add(animationMetadata);

            return rectangle;
        }).map(r -> r).forEach(group.getChildren()::add);

        seconds.setStyle("-fx-background-color: #FFFFFF;");
    }
}
