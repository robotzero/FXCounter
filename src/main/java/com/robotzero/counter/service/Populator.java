package com.robotzero.counter.service;

import com.robotzero.counter.domain.*;
import io.reactivex.subjects.Subject;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.reactfx.EventSource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Populator {

    private IntegerProperty cellSize = new SimpleIntegerProperty(60);
    private final Clocks clocks;
    private final EventSource[] clocksEvents;
    private final Subject<Integer> deltaStreamSeconds;
    private final Subject<Integer> deltaStreamMinutes;
    private final Subject<Integer> deltaStreamHours;
    private ObjectProperty<Font> fontTracking = new SimpleObjectProperty<>(Font.getDefault());

    public Populator(final Clocks clocks, final List<Subject<Integer>> deltaStreams, EventSource ...clocksEvents) {
        this.clocks = clocks;
        this.clocksEvents = clocksEvents;
        this.deltaStreamSeconds = deltaStreams.get(0);
        this.deltaStreamMinutes = deltaStreams.get(1);
        this.deltaStreamHours = deltaStreams.get(2);
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

            if (rect.getId() != null && (rect.getId().contains("strokeInsideDark"))) {
                rect.strokeWidthProperty().bind(rect.widthProperty().multiply(0.02));
                rect.heightProperty().bind(cellSize.multiply(3));
                rect.translateYProperty().bind(cellSize.add(1));
            }

            rect.widthProperty().bind(stack.widthProperty().subtract(stack.widthProperty().multiply(0.09)));
        });

        List cc = stack.getChildren().stream().filter(child -> child.getClass().equals(VBox.class)).map(vbox -> {
            Cell cell = ((VBox) vbox).getChildren().stream().map(sp -> {
                Rectangle rectangle = (Rectangle) ((StackPane) sp).getChildren().get(0);
                Text text = (Text) ((StackPane) sp).getChildren().get(1);
                String id = "";
                Subject<Integer> deltaStream = null;

                rectangle.widthProperty().bind(stack.widthProperty().subtract(stack.widthProperty().multiply(0.09)));
                rectangle.heightProperty().bind(stack.heightProperty().divide(4).multiply(0.8));
                cellSize.bind(rectangle.heightProperty());
                text.translateYProperty().bind(rectangle.heightProperty().subtract(fontTracking.get().getSize()).multiply(0.23));

                rectangle.widthProperty().addListener((observableValue, oldWidth, newWidth) -> fontTracking.set(Font.font(newWidth.doubleValue() / 3)));

                text.fontProperty().bind(fontTracking);
                if (stack.getId().equals("paneSeconds")) {
                    deltaStream = deltaStreamSeconds;
                    id = vbox.getId() + "seconds";
                }

                if (stack.getId().equals("paneMinutes")) {
                    deltaStream = deltaStreamMinutes;
                    id = vbox.getId() + "minutes";
                }

                if (stack.getId().equals("paneHours")) {
                    deltaStream = deltaStreamHours;
                    id = vbox.getId() + "hours";
                }

                text.setId(id);
                rectangle.setId(id);
                TranslateTransition translateTransition = new TranslateTransition();
                translateTransition.setNode(vbox);

                return new Cell((VBox) vbox, new Location(), text, translateTransition, deltaStream, cellSize);
            }).findFirst().get();

            return cell;
        }).collect(Collectors.toList());

        Rectangle clipRectangle = new Rectangle();
        clipRectangle.heightProperty().bind(cellSize.multiply(3).add(2));
        clipRectangle.widthProperty().bind(stack.widthProperty());
        clipRectangle.setX(0);
        clipRectangle.yProperty().bind(cellSize);
        stack.setClip(clipRectangle);

        if (stack.getId().contains("Seconds")) {
            Column column = new Column(cc, clocks, ColumnType.SECONDS, clocksEvents[0]);
            column.setLabels();
            return column;
        }

        if (stack.getId().contains("Minutes")) {
            Column column = new Column(cc, clocks, ColumnType.MINUTES, clocksEvents[1]);
            column.setLabels();
            return column;
        }

        Column column = new Column(cc, clocks, ColumnType.HOURS, clocksEvents[2]);
        column.setLabels();
        return column;
    }
}
