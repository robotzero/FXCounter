package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.service.LocationService;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryCellStateRepository implements CellStateRepository {
    private Map<ColumnType, Map<Integer, CellState>> currentCellsState;
    private ArrayDeque<CellState> seconds;
    private ArrayDeque<CellState> previous;

    @Override
    public void initialize(Map<ColumnType, Map<Integer, CellState>> currentCellsState) {
        this.seconds = currentCellsState.get(ColumnType.SECONDS).entrySet().stream().map(entry -> {
            return entry.getValue();
        }).sorted((i1, i2) -> {
            Integer test = (int) i1.getCurrentLocation().getFromY();
            Integer test2 = (int) i2.getCurrentLocation().getFromY();
            return test.compareTo(test2);
        }).collect(Collectors.toCollection(ArrayDeque::new));

        this.previous = this.seconds.clone();
        this.currentCellsState = currentCellsState;
    }

    private void update(ColumnType columnType, int id, double position, double oldPosition) {
        CellState cellState = currentCellsState.get(columnType).get(id);
        CellState newCellState = cellState.createNew(position, oldPosition);
        Map<Integer, CellState> currentList = currentCellsState.get(columnType);
        currentList.put(id, newCellState);
    }

    private void update(ColumnType columnType, int id, Direction direction) {
        CellState cellState = currentCellsState.get(columnType).get(id);
        CellState newCellState = cellState.createNew(direction);
        Map<Integer, CellState> currentList = currentCellsState.get(columnType);
        currentList.put(id, newCellState);
    }

    @Override
    public void update(Direction direction, ColumnType columnType) {
        currentCellsState.entrySet().stream().filter(entry -> entry.getKey() == columnType)
                .flatMap(entry -> {
                    return entry.getValue().entrySet().stream();
                }).map(cellState -> {
            return cellState.getValue();
        })
                .forEach(entry -> {
                    this.update(columnType, entry.getId(), direction);
                });
    }

    @Override
    public void update(LocationService locationService, ColumnType columnType, double delta) {
        currentCellsState.entrySet().stream().filter(entry -> entry.getKey() == columnType)
                .flatMap(entry -> {
                    return entry.getValue().entrySet().stream();
                }).map(cellState -> {
                    return cellState.getValue();
                })
                .forEach(entry -> {
                    double fromY = locationService.calculateFromY(new SimpleIntegerProperty(90), delta, entry.getCurrentLocation().getToY());
                    double toY = locationService.calculateToY(new SimpleIntegerProperty(90), delta, entry.getCurrentLocation().getToY());
//                    System.out.println("Cell with " + entry.getId() + " is updated to " + "FROM Y " + fromY + " TO Y " + toY);
                    this.update(columnType, entry.getId(), fromY, toY);
                });

//        this.seconds = currentCellsState.get(ColumnType.SECONDS).entrySet().stream().map(entry -> {
//            return entry.getValue();
//        }).sorted((i1, i2) -> {
//            Integer test = (int) i1.getCurrentLocation().getFromY();
//            Integer test2 = (int) i2.getCurrentLocation().getFromY();
//            return test.compareTo(test2);
//        }).collect(Collectors.toCollection(ArrayDeque::new));

        ArrayDeque<CellState> clone = this.seconds.clone();
        if (delta < 0) {
            CellState cellState = this.seconds.removeFirst();
            this.seconds.addLast(cellState);
        }

        if (delta > 0) {
            seconds.forEach(c -> {
//                System.out.println(c.getCurrentLocation().getFromY());
//                System.out.println(c.getCurrentLocation().getToY());
            });
            CellState cellState = this.seconds.getLast();
            CellState c = this.currentCellsState.get(ColumnType.SECONDS).get(cellState.getId());
            if (c.getCurrentLocation().getFromY() == 180 && c.getCurrentLocation().getToY() == 270) {

            } else {
                this.seconds.getLast();
                System.out.println(c.getCurrentLocation().getFromY());
                System.out.println(c.getCurrentLocation().getToY());
                this.seconds.addFirst(cellState);
            }
        }

        this.previous = clone;
    }

    @Override
    public Optional<CellState> get(int id) {
        return currentCellsState.values().stream().map(entry -> entry.get(id)).findFirst();
    }

    @Override
    public List<CellState> getChangeCellStates() {
        List<CellState> changeCellStates = currentCellsState.entrySet().stream().flatMap(entry -> {
            return entry.getValue().values().stream().filter(CellState::isChangeable);
        }).collect(Collectors.toList());

        return changeCellStates;
    }

    @Override
    public CellState getChangeCellState(double delta) {
        if (delta < 0) {
            return this.seconds.getLast();
        }
        return this.seconds.getLast();
    }

    @Override
    public Map<Integer, CellState> getAll(ColumnType columnType) {
        return this.currentCellsState.get(columnType);
    }

    @Override
    public String toString() {
        return "InMemoryCellStateRepository{" +
                "currentCellsState=" + currentCellsState +
                '}';
    }
}
