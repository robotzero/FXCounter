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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Populator {

    private IntegerProperty cellSize = new SimpleIntegerProperty(60);
    private ObjectProperty<Font> fontTracking = new SimpleObjectProperty<>(Font.getDefault());

    public Map<ColumnType, Column> timerColumns(GridPane gridPane) {
        Collector<Map.Entry<ColumnType, List<Cell>>, HashMap<ColumnType, Column>, HashMap<ColumnType, Column>> timerColumnsCollector = Collector.of(
                HashMap::new,
                (intermediateContainer, cellList) -> {
                    intermediateContainer.put(cellList.getKey(), new Column(cellList.getValue()));
                },
                (finalContainer, intermediateContainer) -> {
                    Optional.of(intermediateContainer.get(ColumnType.SECONDS)).ifPresent(column -> {
                        finalContainer.put(ColumnType.SECONDS, column);
                    });
                    Optional.of(intermediateContainer.get(ColumnType.MINUTES)).ifPresent(column -> {
                        finalContainer.put(ColumnType.MINUTES, column);
                    });
                    Optional.of(intermediateContainer.get(ColumnType.HOURS)).ifPresent(column -> {
                        finalContainer.put(ColumnType.HOURS, column);
                    });

                    return finalContainer;
                }
        );

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
                    return new Cell(vbox, new LocationService(), ((Text) vbox.getChildren().get(0)), translateTransition, new SimpleIntegerProperty(90), ColumnType.valueOf(node.getParent().getId().toUpperCase()));
                }).collect(Collectors.groupingBy(Cell::getColumnType)).entrySet().stream().collect(timerColumnsCollector);
    }

    public Map<ColumnType, Map<Integer, CellState>> cellState(GridPane gridPane) {
        //@TODO change to rxjava type of initialization.
        return gridPane.getChildrenUnmodifiable().filtered(
                node -> node.getClass().equals(StackPane.class) &&
                        (node.getId().equals("seconds") || node.getId().equals("minutes") || node.getId().equals("hours"))
        ).stream()
                .flatMap(stackPane -> ((StackPane) stackPane).getChildren().stream())
                .filter(node -> node.getClass().equals(VBox.class))
                .map(node -> {
                    VBox vBox = (VBox) node;
                    return new CellState(
                            Integer.valueOf(vBox.getId()),
                            new Location(vBox.getTranslateY(), vBox.getTranslateY()),
                            new Location(vBox.getTranslateY(), vBox.getTranslateY()),
                            new Direction(ColumnType.valueOf(node.getParent().getId().toUpperCase()), DirectionType.VOID),
                            new Direction(ColumnType.valueOf(node.getParent().getId().toUpperCase()), DirectionType.VOID),
                            ColumnType.valueOf(node.getParent().getId().toUpperCase())
                    );
                }).collect(Collectors.groupingBy(CellState::getColumnType, Collector.of(
                        ConcurrentHashMap::new,
                        (map, cellState) -> {
                            map.put(cellState.getId(), cellState);
                        }, (left, right) -> {
                            left.putAll(right); return left;
                        }
                    )
                )
        );
    }

}
