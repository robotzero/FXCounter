package com.robotzero.counter.service;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.domain.clock.Clocks;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        stack.getChildren().stream().filter(child -> child.getClass().equals(Rectangle.class)).forEach(r -> {
            Rectangle rect = (Rectangle) r;
            if (rect.getId() != null && rect.getId().equals("bottomWhite")) {
//                rect.heightProperty().bind(cellSize.multiply(3).subtract(10));
//                rect.translateYProperty().bind(cellSize.add(5));
            } else {
//                rect.heightProperty().bind(cellSize.multiply(3));
//                rect.translateYProperty().bind(cellSize);
            }

            if (rect.getId() != null && (rect.getId().contains("strokeInsideDark"))) {
//                rect.strokeWidthProperty().bind(rect.widthProperty().multiply(0.02));
//                rect.heightProperty().bind(cellSize.multiply(3));
//                rect.translateYProperty().bind(cellSize.add(1));
            }

//            rect.widthProperty().bind(stack.widthProperty().subtract(stack.widthProperty().multiply(0.09)));
        });

        List cc = stack.getChildren().stream().filter(child -> child.getClass().equals(VBox.class)).flatMap(vBox -> ((VBox)vBox).getChildren().stream()).map(stackPane -> {
            Rectangle rectangle = (Rectangle) ((StackPane) stackPane).getChildren().get(0);
            Text text = (Text) ((StackPane) stackPane).getChildren().get(1);
            String id = "";
            Optional<Subject<Integer>> deltaStream = Optional.empty();

//            rectangle.widthProperty().bind(stack.widthProperty().subtract(stack.widthProperty().multiply(0.09)));
//            rectangle.heightProperty().bind(stack.heightProperty().divide(4).multiply(0.8));
//            cellSize.bind(rectangle.heightProperty());
//            text.translateYProperty().bind(rectangle.heightProperty().subtract(fontTracking.get().getSize()).multiply(0.23));

//            rectangle.widthProperty().addListener((observableValue, oldWidth, newWidth) -> fontTracking.set(Font.font(newWidth.doubleValue() / 3)));

//            text.fontProperty().bind(fontTracking);
            if (stack.getId().equals("paneSeconds")) {
                deltaStream = Optional.ofNullable(deltaStreamSeconds);
                id = stackPane.getParent().getId() + "seconds";
            }

            if (stack.getId().equals("paneMinutes")) {
                deltaStream = Optional.ofNullable(deltaStreamMinutes);
                id = stackPane.getParent().getId() + "minutes";
            }

            if (stack.getId().equals("paneHours")) {
                deltaStream = Optional.ofNullable(deltaStreamHours);
                id = stackPane.getParent().getId() + "hours";
            }

            text.setId(id);
            rectangle.setId(id);
            TranslateTransition translateTransition = new TranslateTransition();
            translateTransition.setNode(stackPane.getParent());

            return new Cell((VBox) stackPane.getParent(), new Location(), text, translateTransition, deltaStream.orElseThrow(() -> new RuntimeException("No delta steam available.")), cellSize);
            }).collect(Collectors.toList());


//        Rectangle clipRectangle = new Rectangle();
//        clipRectangle.heightProperty().bind(cellSize.multiply(3).add(2));
//        clipRectangle.widthProperty().bind(stack.widthProperty());
//        clipRectangle.setX(0);
//        clipRectangle.yProperty().bind(cellSize);
//        stack.setClip(clipRectangle);

        if (stack.getId().contains("Seconds")) {
            Column column = new Column(cc, clocks, ColumnType.SECONDS, clocksEvents[0]);
//            column.setLabels();
            return column;
        }

        if (stack.getId().contains("Minutes")) {
            Column column = new Column(cc, clocks, ColumnType.MINUTES, clocksEvents[1]);
//            column.setLabels();
            return column;
        }

        Column column = new Column(cc, clocks, ColumnType.HOURS, clocksEvents[2]);
//        column.setLabels();
        return column;
    }

    public Map<ColumnType, Column> timerColumns(GridPane gridPane, Clocks clocks) {
        List<Cell> seconds = gridPane.getChildrenUnmodifiable()
                .filtered(node -> node.getClass().equals(StackPane.class) && node.getId().equals("seconds"))
                .stream()
                .flatMap(stackPane -> ((StackPane) stackPane).getChildren().stream())
                .filter(node -> node.getClass().equals(VBox.class))
                .map(node -> {
                    TranslateTransition translateTransition = new TranslateTransition();
                    VBox vbox = (VBox) node;
                    translateTransition.setNode(vbox);
                    return new Cell(vbox, new Location(), ((Text) vbox.getChildren().get(0)), translateTransition, deltaStreamSeconds, new SimpleIntegerProperty(90));
                }).collect(Collectors.toList());

        List<Cell> minutes = gridPane.getChildrenUnmodifiable()
                .filtered(node -> node.getClass().equals(StackPane.class) && node.getId().equals("minutes"))
                .stream()
                .flatMap(stackPane -> ((StackPane) stackPane).getChildren().stream())
                .filter(node -> node.getClass().equals(VBox.class))
                .map(node -> {
                    TranslateTransition translateTransition = new TranslateTransition();
                    VBox vbox = (VBox) node;
                    translateTransition.setNode(vbox);
                    return new Cell(vbox, new Location(), ((Text) vbox.getChildren().get(0)), translateTransition, deltaStreamMinutes, new SimpleIntegerProperty(90));
                }).collect(Collectors.toList());

        List<Cell> hours = gridPane.getChildrenUnmodifiable()
                .filtered(node -> node.getClass().equals(StackPane.class) && node.getId().equals("hours"))
                .stream()
                .flatMap(stackPane -> ((StackPane) stackPane).getChildren().stream())
                .filter(node -> node.getClass().equals(VBox.class))
                .map(node -> {
                    TranslateTransition translateTransition = new TranslateTransition();
                    VBox vbox = (VBox) node;
                    translateTransition.setNode(vbox);
                    return new Cell(vbox, new Location(), ((Text) vbox.getChildren().get(0)), translateTransition, deltaStreamHours, new SimpleIntegerProperty(90));
                }).collect(Collectors.toList());

        Map<ColumnType, Column> timerColumns = new HashMap<>();
        timerColumns.put(ColumnType.SECONDS, new Column(seconds, clocks, ColumnType.SECONDS, clocksEvents[0]));
        timerColumns.put(ColumnType.MINUTES, new Column(minutes, clocks, ColumnType.MINUTES, clocksEvents[1]));
        timerColumns.put(ColumnType.HOURS, new Column(hours, clocks, ColumnType.HOURS, clocksEvents[2]));

        return timerColumns;
    }

}
