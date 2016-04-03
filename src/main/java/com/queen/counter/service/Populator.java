package com.queen.counter.service;

import com.queen.counter.clock.ClockPresenter;
import com.queen.counter.domain.AnimationMetadata;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Populator {

    public List<AnimationMetadata> populateSeconds(LocalTime userTime, Group group, String label) {
         List<AnimationMetadata> animationMetadatas = new ArrayList<>();
         Random random = new Random();

         IntStream.range(0, ClockPresenter.blockCount).mapToObj(i -> {
         Rectangle rectangle = new Rectangle(ClockPresenter.cellsize, 0, ClockPresenter.cellsize, ClockPresenter.cellsize);
         rectangle.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 1));
         rectangle.setStrokeType(StrokeType.INSIDE);
         rectangle.setStroke(Color.BLACK);
         rectangle.setTranslateY(ClockPresenter.cellsize * i);

         AnimationMetadata animationMetadata = new AnimationMetadata(rectangle);
         animationMetadatas.add(animationMetadata);

         Text t;

         if (label.equals("seconds")) {
             t = new Text(userTime.getSecond() - i + 2 + "");
         } else {
             t = new Text(userTime.getMinute() - i + 2 + "");
         }

         t.setFont(Font.font(20));

         t.xProperty().bind(rectangle.xProperty().add(rectangle.widthProperty().divide(2)));

         t.yProperty().bind(rectangle.translateYProperty().add(rectangle.heightProperty().divide(2)));
         t.setTextAlignment(TextAlignment.CENTER);
         t.setTextOrigin(VPos.CENTER);

         List<Node> array = new ArrayList<>();
         array.add(rectangle);
         array.add(t);
         String id = (random.nextInt(100) + label);
         t.setId(id);
         rectangle.setId(id);
         return array;
         }).map(arr -> arr).forEach(a -> group.getChildren().addAll(a));

         return animationMetadatas;
    }
}
