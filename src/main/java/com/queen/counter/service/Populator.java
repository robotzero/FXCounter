package com.queen.counter.service;

import com.queen.counter.domain.*;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
        stack.getChildren().stream().filter(child -> child.getClass().equals(Rectangle.class)).map(reg -> reg).forEach(r -> {
            Rectangle rect = (Rectangle) r;
            if (rect.getId() != null && rect.getId().equals("bottomWhite")) {
                rect.heightProperty().bind(cellSize.multiply(3).subtract(10));
                rect.translateYProperty().bind(cellSize.add(5));
            } else {
                rect.heightProperty().bind(cellSize.multiply(3));
                rect.translateYProperty().bind(cellSize);
            }

            rect.widthProperty().bind(stack.widthProperty().subtract(stack.widthProperty().multiply(0.09)));
        });

        List cc = stack.getChildren().stream().filter(child -> child.getClass().equals(VBox.class)).map(vbox -> {
            stack.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(0), new Insets(0, 0, 0, 0))));
            Cell cell = ((VBox) vbox).getChildren().stream().map(sp -> {
                ((VBox) vbox).setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(0), new Insets(0, 0, 0, 0))));
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

                rectangle.widthProperty().bind(stack.widthProperty().subtract(stack.widthProperty().multiply(0.09)));
                rectangle.heightProperty().bind(stack.heightProperty().divide(4).multiply(0.8));
                cellSize.bind(rectangle.heightProperty());
                text.translateYProperty().bind(rectangle.heightProperty().subtract(fontSize).multiply(0.23));
                fontSize.bind(rectangle.widthProperty().add(rectangle.heightProperty()).divide(4.5));
                rectangle.setFill(Color.TRANSPARENT);
                if (stack.getId().equals("paneSeconds")) {
                    text.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), ";"
                            ,"-fx-base: rgb(100,100,100);", "-fx-effect: dropshadow( three-pass-box , rgba(255, 255, 255, 0.3), 1, 0.0, 1, 1 );"));
                } else {
                    text.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), ";"
                            ,"-fx-base: rgb(255,255,255);", "-fx-effect: dropshadow( three-pass-box , rgba(0, 0, 0, 1.6), 1, 0.0, 2, 2 );"));
                }
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
}
