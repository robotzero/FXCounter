package com.robotzero.counter.service;

import com.robotzero.counter.domain.*;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;

public class Populator {
    private final Function<Class, Predicate<Node>> nodeClassPredicateFactory = (clazz) -> {
      return (node) -> node.getClass().equals(clazz);
    };
    private final Function<ColumnType, Predicate<Node>> nodeTypePredicateFactory = (columnType -> {
      return (node) -> node.getId().equals(columnType.name().toLowerCase());
    });

    public Map<ColumnType, Column> timerColumns(GridPane gridPane) {

        return gridPane.getChildrenUnmodifiable().filtered(
            nodeClassPredicateFactory.apply(StackPane.class)
                .and(
                    nodeTypePredicateFactory.apply(ColumnType.SECONDS)
                        .or(nodeTypePredicateFactory.apply(ColumnType.MINUTES))
                        .or(nodeTypePredicateFactory.apply(ColumnType.HOURS))
                )
        ).stream()
                .flatMap(stackPane -> ((StackPane) stackPane).getChildren().stream())
                .filter(nodeClassPredicateFactory.apply(VBox.class))
                .map(node -> {
                    TranslateTransition translateTransition = new TranslateTransition();
                    VBox vbox = (VBox) node;
                    translateTransition.setNode(vbox);
                    CellState cellState = new CellState(
                      Integer.parseInt(vbox.getId()),
                      new Location(vbox.getTranslateY(), vbox.getTranslateY()),
                      new Location(vbox.getTranslateY(), vbox.getTranslateY()),
                      DirectionType.VOID,
                      DirectionType.VOID,
                      ColumnType.valueOf(node.getParent().getId().toUpperCase()),
                      CellStatePosition.NONCHANGABLE
                  );
                    return new Cell(
                        Integer.parseInt(vbox.getId()),
                        ((Text) vbox.getChildren().get(0)),
                        translateTransition,
                        new SimpleIntegerProperty(90),
                        ColumnType.valueOf(node.getParent().getId().toUpperCase()),
                        cellState
                    );
                }).collect(Collectors.groupingBy(Cell::getColumnType, collectingAndThen(Collectors.toMap((key) -> key.getId(), (value) -> value), Column::new)));
    }

    public Map<ColumnType, List<CellState>> cellState(GridPane gridPane) {
        return gridPane.getChildrenUnmodifiable().filtered(
            nodeClassPredicateFactory.apply(StackPane.class)
                .and(
                    nodeTypePredicateFactory.apply(ColumnType.SECONDS)
                        .or(nodeTypePredicateFactory.apply(ColumnType.MINUTES))
                        .or(nodeTypePredicateFactory.apply(ColumnType.HOURS))
            )
        ).stream()
                .flatMap(stackPane -> ((StackPane) stackPane).getChildren().stream())
                .filter(nodeClassPredicateFactory.apply(VBox.class))
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
