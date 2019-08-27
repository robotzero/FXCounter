package com.robotzero.counter.service;

import com.robotzero.counter.domain.*;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;

public class Populator {
    public Map<ColumnType, Column> timerColumns(GridPane gridPane) {
        return gridPane.getChildrenUnmodifiable().filtered(
                node -> node.getClass().equals(StackPane.class) &&
                        (node.getId().equals("seconds") || node.getId().equals("minutes") || node.getId().equals("hours"))
        ).stream()
                .flatMap(stackPane -> ((StackPane) stackPane).getChildren().stream())
                .filter(node -> node.getClass().equals(VBox.class))
                .map(node -> {
                    TranslateTransition translateTransition = new TranslateTransition();
                    VBox vbox = (VBox) node;
                    translateTransition.setNode(vbox);
                    return new Cell(vbox, ((Text) vbox.getChildren().get(0)), translateTransition, new SimpleIntegerProperty(90), ColumnType.valueOf(node.getParent().getId().toUpperCase()));
                }).collect(Collectors.groupingBy(Cell::getColumnType, collectingAndThen(Collectors.toList(), Column::new)));
    }

    public Map<ColumnType, List<CellState>> cellState(GridPane gridPane) {
        return gridPane.getChildrenUnmodifiable().filtered(
                node -> node.getClass().equals(StackPane.class) &&
                        (node.getId().equals("seconds") || node.getId().equals("minutes") || node.getId().equals("hours"))
        ).stream()
                .flatMap(stackPane -> ((StackPane) stackPane).getChildren().stream())
                .filter(node -> node.getClass().equals(VBox.class))
                .map(node -> {
                    VBox vBox = (VBox) node;
                    return new CellState(
                            Integer.parseInt(vBox.getId()),
                            new Location(vBox.getTranslateY(), vBox.getTranslateY()),
                            new Location(vBox.getTranslateY(), vBox.getTranslateY()),
                            DirectionType.VOID,
                            DirectionType.VOID,
                            ColumnType.valueOf(node.getParent().getId().toUpperCase()),
                            CellStatePosition.NONCHANGABLE
                    );
                }).sorted((i1, i2) -> {
                    Integer test = (int) i1.getCurrentLocation().getFromY();
                    Integer test2 = (int) i2.getCurrentLocation().getFromY();
                    return test.compareTo(test2);
                }).collect(Collectors.groupingBy(CellState::getColumnType, Collectors.toList())
        );
    }
}
