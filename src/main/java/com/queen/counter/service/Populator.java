package com.queen.counter.service;

import com.queen.counter.clock.ClockPresenter;
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
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Populator {

    public void populate(LocalTime userTime, Stream<Group> groups) {
        final Random random = new Random();

            groups.forEach(g -> {
                IntStream.range(0, ClockPresenter.blockCount).mapToObj(i -> {
                    Rectangle rectangle = new Rectangle(ClockPresenter.cellsize, 0, ClockPresenter.cellsize, ClockPresenter.cellsize);
                    rectangle.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 1));
                    rectangle.setStrokeType(StrokeType.INSIDE);
                    rectangle.setStroke(Color.BLACK);
                    rectangle.setTranslateY(ClockPresenter.cellsize * i);

                    final Text t = new Text();

                    String id = "";
                    if (g.getId().equals("group")) {
                        t.setText(userTime.getSecond() - i + 2 + "");
                        id = (random.nextInt(100) + "seconds");
                    }

                    if (g.getId().equals("minutesgroup")) {
                        t.setText(userTime.getMinute() - i + 2 + "");
                        id = (random.nextInt(100) + "minutes");
                    }

                    t.setFont(Font.font(20));

                    t.xProperty().bind(rectangle.xProperty().add(rectangle.widthProperty().divide(2)));

                    t.yProperty().bind(rectangle.translateYProperty().add(rectangle.heightProperty().divide(2)));
                    t.setTextAlignment(TextAlignment.CENTER);
                    t.setTextOrigin(VPos.CENTER);

                    t.setId(id);
                    rectangle.setId(id);

                    g.getChildren().addAll(rectangle, t);
                    return 0;
                }).count();
            });
    }
}
