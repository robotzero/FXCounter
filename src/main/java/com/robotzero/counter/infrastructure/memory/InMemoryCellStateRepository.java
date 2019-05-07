package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.service.LocationService;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryCellStateRepository implements CellStateRepository {
    private Map<ColumnType, Map<Integer, CellState>> currentCellsState;

    @Override
    public void initialize(Map<ColumnType, Map<Integer, CellState>> currentCellsState) {
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
