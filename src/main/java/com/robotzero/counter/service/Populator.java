package com.robotzero.counter.service;

import com.robotzero.counter.domain.*;
import io.reactivex.subjects.Subject;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Populator {

    private IntegerProperty cellSize = new SimpleIntegerProperty(60);
    private final Subject<Integer>[] clocksEvents;
    private final Subject<Direction> deltaStreamSeconds;
    private final Subject<Direction> deltaStreamMinutes;
    private final Subject<Direction> deltaStreamHours;
    private ObjectProperty<Font> fontTracking = new SimpleObjectProperty<>(Font.getDefault());

    public Populator(final List<Subject<Direction>> deltaStreams, Subject<Integer> ...clocksEvents) {
        this.clocksEvents = clocksEvents;
        this.deltaStreamSeconds = deltaStreams.get(0);
        this.deltaStreamMinutes = deltaStreams.get(1);
        this.deltaStreamHours = deltaStreams.get(2);
    }

    public Map<ColumnType, Column> timerColumns(GridPane gridPane) {
        List<Cell> seconds = gridPane.getChildrenUnmodifiable()
                .filtered(node -> node.getClass().equals(StackPane.class) && node.getId().equals("seconds"))
                .stream()
                .flatMap(stackPane -> ((StackPane) stackPane).getChildren().stream())
                .filter(node -> node.getClass().equals(VBox.class))
                .map(node -> {
                    TranslateTransition translateTransition = new TranslateTransition();
                    VBox vbox = (VBox) node;
                    translateTransition.setNode(vbox);
                    translateTransition.setDuration(Duration.millis(600));
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
        timerColumns.put(ColumnType.SECONDS, new Column(seconds, ColumnType.SECONDS, clocksEvents[0]));
        timerColumns.put(ColumnType.MINUTES, new Column(minutes, ColumnType.MINUTES, clocksEvents[1]));
        timerColumns.put(ColumnType.HOURS, new Column(hours, ColumnType.HOURS, clocksEvents[2]));

        return timerColumns;
    }

}
