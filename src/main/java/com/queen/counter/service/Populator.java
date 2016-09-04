package com.queen.counter.service;

import com.queen.counter.domain.*;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import org.reactfx.EventSource;
import org.reactfx.util.Tuple2;

import java.util.List;
import java.util.stream.Collectors;

public class Populator {

    private IntegerProperty cellSize = new SimpleIntegerProperty(60);
    private IntegerProperty fontSize = new SimpleIntegerProperty(40);
    private final Clocks clocks;
    private final EventSource[] clocksEvents;
    private final EventSource<Tuple2<Integer, ColumnType>> deltaStream;

    public Populator(final Clocks clocks, final EventSource<Tuple2<Integer, ColumnType>> deltaStream, EventSource ...clocksEvents) {
        this.clocks = clocks;
        this.deltaStream = deltaStream;
        this.clocksEvents = clocksEvents;
    }

    public Column create(StackPane stack) {
        List cc = stack.getChildren().stream().filter(child -> child.getClass().equals(VBox.class)).map(vbox -> {
            Cell cell = ((VBox) vbox).getChildren().stream().map(sp -> {
                Rectangle rectangle = (Rectangle) ((StackPane) sp).getChildren().get(0);
                Text text = (Text) ((StackPane) sp).getChildren().get(1);
                String id = "";
                if (stack.getId().equals("paneSeconds")) {
                    id = vbox.getId() + "seconds";
                }

                if (stack.getId().equals("paneMinutes")) {
                    id = vbox.getId() + "minutes";
                }

                if (stack.getId().equals("paneHours")) {
                    id = vbox.getId() + "hours";
                }

                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeType(StrokeType.INSIDE);
                text.translateYProperty().bind(rectangle.translateYProperty());
                rectangle.widthProperty().bind(stack.widthProperty().subtract(stack.widthProperty().multiply(0.3)));
                rectangle.heightProperty().bind(stack.heightProperty().divide(4).multiply(0.7));
                cellSize.bind(rectangle.heightProperty());
                text.translateYProperty().bind(rectangle.heightProperty().subtract(fontSize).multiply(0.4));
                fontSize.bind(rectangle.widthProperty().add(rectangle.heightProperty()).divide(5));
                text.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), ";"
                        ,"-fx-base: rgb(100,100,100);"));
                text.setId(id);
                rectangle.setId(id);
                TranslateTransition translateTransition = new TranslateTransition();
                translateTransition.setNode(vbox);
                return new Cell((VBox) vbox, new Location(new Point2D(rectangle.getTranslateX(), rectangle.getTranslateY())), text, translateTransition, deltaStream, cellSize);
            }).findFirst().get();

            return cell;
        }).collect(Collectors.toList());

        Rectangle clipRectangle = new Rectangle();
        clipRectangle.heightProperty().bind(cellSize.multiply(3));
        clipRectangle.widthProperty().bind(stack.widthProperty());
        clipRectangle.setX(0);
        clipRectangle.yProperty().bind(cellSize);
        stack.setClip(clipRectangle);

        if (stack.getId().contains("Seconds")) {
            return new Column(cc, clocks, ColumnType.SECONDS, clocksEvents[0]);
        }

        if (stack.getId().contains("Minutes")) {
            return new Column(cc, clocks, ColumnType.MINUTES, clocksEvents[1]);
        }

        return new Column(cc, clocks, ColumnType.HOURS, clocksEvents[2]);
    }

//    public Column create(Group group, ReadOnlyDoubleProperty widthProperty, ReadOnlyDoubleProperty heightProperty) {
//
//        final Random random = new Random();
//        List<Cell> list =  IntStream.range(0, blockCount).mapToObj(i -> {
//            Rectangle rectangle = new Rectangle(cellsize, 0, cellsize, cellsize);
//            rectangle.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 1));
//            rectangle.setStrokeType(StrokeType.INSIDE);
//            rectangle.setStroke(Color.BLACK);
//            rectangle.setTranslateY(cellsize * i);
//
//            final Text label = new Text();
//
//            String id = "";
//
//            if (group.getId().equals("seconds")) {
//                id = i + "seconds";
//            }
//
//            if (group.getId().equals("minutes")) {
//                id = i + "minutes";
//            }
//
//            rectangle.xProperty().bind(widthProperty.divide(2).subtract(rectangle.widthProperty().divide(2)));
//            rectangle.yProperty().bind(heightProperty.divide(2).subtract(rectangle.heightProperty().multiply(blockCount).divide(2)));
//
//            label.setFont(Font.font(20));
//
////            label.xProperty().bind(rectangle.widthProperty().divide(2).subtract(label.xProperty().divide(2)));
//            label.xProperty().bind(rectangle.xProperty().add(rectangle.widthProperty().divide(2)));
//            label.yProperty().bind(rectangle.translateYProperty().add(rectangle.heightProperty().divide(2)));
//            label.setTextAlignment(TextAlignment.CENTER);
//            label.setTextOrigin(VPos.CENTER);
//
//            label.setId(id);
//            rectangle.setId(id);
//
////            group.getChildren().addAll(rectangle, label);
//
//            TranslateTransition translateTransition = new TranslateTransition();
//            translateTransition.setNode(rectangle);
//            return new Cell(rectangle, new Location(new Point2D(rectangle.getTranslateX(), rectangle.getTranslateY())), label, translateTransition, deltaStream);
//        }).collect(Collectors.toList());
//
//        if (group.getId().equals("seconds")) {
//            return new Column(list, clocks, ColumnType.SECONDS, clocksEvents[0]);
//        }
//
//        if (group.getId().equals("minutes")) {
//            return new Column(list, clocks, ColumnType.MINUTES, clocksEvents[1]);
//        }
//
//        return new Column(list, clocks, ColumnType.HOURS, clocksEvents[2]);
//    }
}
