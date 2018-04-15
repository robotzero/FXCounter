package com.robotzero.counter.service;

import com.robotzero.counter.domain.*;
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
    private ObjectProperty<Font> fontTracking = new SimpleObjectProperty<>(Font.getDefault());

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
                    return new Cell(vbox, new Location(), ((Text) vbox.getChildren().get(0)), translateTransition, new SimpleIntegerProperty(90), ColumnType.SECONDS);
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
                    translateTransition.setDuration(Duration.millis(600));
                    return new Cell(vbox, new Location(), ((Text) vbox.getChildren().get(0)), translateTransition, new SimpleIntegerProperty(90), ColumnType.MINUTES);
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
                    translateTransition.setDuration(Duration.millis(600));
                    return new Cell(vbox, new Location(), ((Text) vbox.getChildren().get(0)), translateTransition, new SimpleIntegerProperty(90), ColumnType.HOURS);
                }).collect(Collectors.toList());

        Map<ColumnType, Column> timerColumns = new HashMap<>();
        timerColumns.put(ColumnType.SECONDS, new Column(seconds, ColumnType.SECONDS));
        timerColumns.put(ColumnType.MINUTES, new Column(minutes, ColumnType.MINUTES));
        timerColumns.put(ColumnType.HOURS, new Column(hours, ColumnType.HOURS));

        return timerColumns;
    }

}
