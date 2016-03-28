package com.king.counter.clock;

import com.king.animator.Animator;
import com.king.configuration.SceneConfiguration;
import com.king.counter.domain.AnimationMetadata;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.populateSeconds();

        EventStream<MouseEvent>buttonClicks = EventStreams.eventsOf(start, MouseEvent.MOUSE_CLICKED);
        EventStream<MouseEvent>stopClicks = EventStreams.eventsOf(stop, MouseEvent.MOUSE_CLICKED);
        EventStream<ScrollEvent>scroll = EventStreams.eventsOf(group, ScrollEvent.ANY);
        EventStream<?> ticks = EventStreams.ticks(Duration.ofMillis(1000));

        scroll.subscribe(scrollEvent -> {
            double delta = scrollEvent.getDeltaY();

            List<AnimationMetadata> l = this.rectangles.stream().map(n -> new AnimationMetadata((Rectangle) n)).collect(Collectors.toList());
            animator.animate(l, delta);

        });

        buttonClicks.subscribe(click -> {
            animator.animate(this.animationMetadatas, 0);
            this.subscribe  = ticks.subscribe((something) -> {
                    List<AnimationMetadata> l = this.rectangles.stream().map(r -> new AnimationMetadata((Rectangle) r)).collect(Collectors.toList());
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

        IntStream.range(0, blockCount).mapToObj(i -> {
            Rectangle rectangle = new Rectangle(cellsize, 0, cellsize, cellsize);
            rectangle.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 1));
            rectangle.setStrokeType(StrokeType.INSIDE);
            rectangle.setStroke(Color.BLACK);
            rectangle.setTranslateY(cellsize * i);

            AnimationMetadata animationMetadata = new AnimationMetadata(rectangle);
            animationMetadatas.add(animationMetadata);
            Text t = new Text(random.nextInt(100) + "");
            t.setFont(Font.font(20));
            
            t.xProperty().bind(rectangle.xProperty().add(rectangle.widthProperty().divide(2)));
            System.out.println(rectangle.xProperty().get());

            t.yProperty().bind(rectangle.translateYProperty().add(rectangle.heightProperty().divide(2)));
            t.setTextAlignment(TextAlignment.CENTER);
            t.setTextOrigin(VPos.CENTER);

            List<Node> array = new ArrayList<>();
            array.add(rectangle);
            array.add(t);
            return array;

        }).map(arr -> arr).forEach(a -> group.getChildren().addAll(a));

        this.rectangles = group.getChildren().stream().filter(n -> n.getClass().equals(Rectangle.class)).collect(Collectors.toList());

        seconds.setStyle("-fx-background-color: #FFFFFF;");
    }
}
