package com.queen.counter.service;

import com.queen.counter.domain.*;
import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.reactfx.EventSource;
import org.reactfx.util.Tuple2;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Populator {

    private final int cellsize = 60;
    private final int blockCount = 4;
    private final Clocks clocks;
    private final EventSource[] clocksEvents;
    private final EventSource<Tuple2<Integer, ColumnType>> deltaStream;

    public Populator(final Clocks clocks, final EventSource<Tuple2<Integer, ColumnType>> deltaStream, EventSource ...clocksEvents) {
        this.clocks = clocks;
        this.deltaStream = deltaStream;
        this.clocksEvents = clocksEvents;
    }

    public Column create(Group group, ReadOnlyDoubleProperty widthProperty, ReadOnlyDoubleProperty heightProperty) {

        final Random random = new Random();
        List<Cell> list =  IntStream.range(0, blockCount).mapToObj(i -> {
            Rectangle rectangle = new Rectangle(cellsize, 0, cellsize, cellsize);
            rectangle.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 1));
            rectangle.setStrokeType(StrokeType.INSIDE);
            rectangle.setStroke(Color.BLACK);
            rectangle.setTranslateY(cellsize * i);

            final Text label = new Text();

            String id = "";

            if (group.getId().equals("seconds")) {
                id = i + "seconds";
            }

            if (group.getId().equals("minutes")) {
                id = i + "minutes";
            }

            rectangle.xProperty().bind(widthProperty.divide(2).subtract(rectangle.widthProperty().divide(2)));
            rectangle.yProperty().bind(heightProperty.divide(2).subtract(rectangle.heightProperty().multiply(blockCount).divide(2)));

            label.setFont(Font.font(20));

//            label.xProperty().bind(rectangle.widthProperty().divide(2).subtract(label.xProperty().divide(2)));
            label.xProperty().bind(rectangle.xProperty().add(rectangle.widthProperty().divide(2)));
            label.yProperty().bind(rectangle.translateYProperty().add(rectangle.heightProperty().divide(2)));
            label.setTextAlignment(TextAlignment.CENTER);
            label.setTextOrigin(VPos.CENTER);

            label.setId(id);
            rectangle.setId(id);

            group.getChildren().addAll(rectangle, label);

            TranslateTransition translateTransition = new TranslateTransition();
            translateTransition.setNode(rectangle);
            return new Cell(rectangle, new Location(new Point2D(rectangle.getTranslateX(), rectangle.getTranslateY())), label, translateTransition, deltaStream);
        }).collect(Collectors.toList());

        if (group.getId().equals("seconds")) {
            return new Column(list, clocks, ColumnType.SECONDS, clocksEvents[0]);
        }

        if (group.getId().equals("minutes")) {
            return new Column(list, clocks, ColumnType.MINUTES, clocksEvents[1]);
        }

        return new Column(list, clocks, ColumnType.HOURS, clocksEvents[2]);
    }
}
