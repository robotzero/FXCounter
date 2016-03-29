package com.king.counter.clock;

import com.king.animator.Animator;
import com.king.configuration.SceneConfiguration;
import com.king.counter.domain.AnimationMetadata;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;

import javax.inject.Inject;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private List<Node> rectangles;

    private IntegerProperty clock = new SimpleIntegerProperty();
    private LocalTime time;

    private List<Node> labels;
    private LocalTime userTime;
    private boolean first = true;
    private BooleanProperty delta = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.populateSeconds();

        delta.addListener((observable, oldValue, newValue) -> {
            first = true;
        });

        EventStream<MouseEvent>buttonClicks = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED);
        EventStream<MouseEvent>stopClicks = EventStreams.eventsOf(stop, MouseEvent.MOUSE_CLICKED);
        EventStream<ScrollEvent> scroll = EventStreams.eventsOf(group, ScrollEvent.SCROLL).suppressible().suspendWhen(animator.isRunning());
        EventStream<?> ticks = EventStreams.ticks(Duration.ofMillis(1000));

        scroll.subscribe(scrollEvent -> {
            final int compare;
            animator.setRunning();
            delta.set(scrollEvent.getDeltaY() < 0);
            if (delta.get()) {
                userTime = userTime.minusSeconds(1);
                if (first) {
                    time = userTime.minusSeconds(1);
                } else {
                    time = time.minusSeconds(1);
                }
                compare = 0;
            } else {
                userTime = userTime.plusSeconds(1);
                if (first) {
                    time = userTime.plusSeconds(1);
                } else {
                    time = time.plusSeconds(1);
                }
                compare = 240;
            }
            clock.set(time.getSecond());
            List<AnimationMetadata> l = this.rectangles.stream().map(n -> new AnimationMetadata((Rectangle) n)).collect(Collectors.toList());
            this.rectangles.stream().filter(r -> r.getTranslateY() == compare).forEach(r -> {
                String id = r.getId();
                Text t = (Text) this.labels.stream().filter(lbl -> lbl.getId().equals(id)).findFirst().get();
                t.setText(clock.get() + "");
            });
            animator.animate(l, scrollEvent.getDeltaY());
        });

        buttonClicks.subscribe(click -> {
            userTime = userTime.minusSeconds(1);
            time = userTime.minusSeconds(1);
            clock.set(time.getSecond());

            this.rectangles.stream().filter(r -> r.getTranslateY() == 0).forEach(r -> {
                String id = r.getId();
                Text t = (Text) this.labels.stream().filter(lbl -> lbl.getId().equals(id)).findFirst().get();
                t.setText(clock.get() + "");
            });

            animator.animate(this.animationMetadatas, 0);
            this.subscribe  = ticks.subscribe((something) -> {
                    userTime = userTime.minusSeconds(1);
                    time = userTime.minusSeconds(1);
                    clock.set(time.getSecond());
                    List<AnimationMetadata> l = this.rectangles.stream().map(r -> new AnimationMetadata((Rectangle) r)).collect(Collectors.toList());
                    this.rectangles.stream().filter(r -> r.getTranslateY() == 0).forEach(r -> {
                        String id = r.getId();
                        Text t = (Text) this.labels.stream().filter(lbl -> lbl.getId().equals(id)).findFirst().get();
                        t.setText(clock.get() + "");
                    });
                    animator.animate(l, 0);
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
        userTime = LocalTime.of(0, 12, 12);

        IntStream.range(0, blockCount).mapToObj(i -> {
            Rectangle rectangle = new Rectangle(cellsize, 0, cellsize, cellsize);
            rectangle.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 1));
            rectangle.setStrokeType(StrokeType.INSIDE);
            rectangle.setStroke(Color.BLACK);
            rectangle.setTranslateY(cellsize * i);

            AnimationMetadata animationMetadata = new AnimationMetadata(rectangle);
            animationMetadatas.add(animationMetadata);

            Text t = new Text(userTime.getSecond() - i + 2 + "");
            t.setFont(Font.font(20));

            t.xProperty().bind(rectangle.xProperty().add(rectangle.widthProperty().divide(2)));

            t.yProperty().bind(rectangle.translateYProperty().add(rectangle.heightProperty().divide(2)));
            t.setTextAlignment(TextAlignment.CENTER);
            t.setTextOrigin(VPos.CENTER);

            List<Node> array = new ArrayList<>();
            array.add(rectangle);
            array.add(t);
            String id = (random.nextInt(100) + "");
            t.setId(id);
            rectangle.setId(id);
            return array;

        }).map(arr -> arr).forEach(a -> group.getChildren().addAll(a));

        this.rectangles = group.getChildren().stream().filter(n -> n.getClass().equals(Rectangle.class)).collect(Collectors.toList());
        this.labels = group.getChildren().stream().filter(t -> t.getClass().equals(Text.class)).collect(Collectors.toList());

        seconds.setStyle("-fx-background-color: #FFFFFF;");
    }
}
