package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.service.LocationService;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryCellStateRepository implements CellStateRepository {
    private Map<ColumnType, Map<Integer, CellState>> currentCellsState;

    @Override
    public void initialize(Map<ColumnType, Map<Integer, CellState>> currentCellsState) {
        this.currentCellsState = currentCellsState;
    }

    @Override
    public void update(ColumnType columnType, int id, double position, double oldPosition, Direction direction) {
        CellState cellState = currentCellsState.get(columnType).get(id);
        CellState newCellState = cellState.createNew(position, oldPosition, direction);
        Map<Integer, CellState> currentList = currentCellsState.get(columnType);
        currentList.remove(id);
        currentList.put(id, newCellState);
    }

    @Override
    public void update(LocationService locationService, Direction direction, ColumnType columnType) {
        currentCellsState.entrySet().stream().filter(entry -> entry.getKey() == columnType)
                .flatMap(entry -> {
                    return entry.getValue().entrySet().stream();
                }).map(cellState -> {
                    return cellState.getValue();
                })
                .forEach(entry -> {
                    double fromY = locationService.calculateFromY(new SimpleIntegerProperty(90), direction.getDirectionType().getDelta(), entry.getNewLocation().getToY());
                    double toY = locationService.calculateToY(new SimpleIntegerProperty(90), direction.getDirectionType().getDelta(), entry.getNewLocation().getToY());
                    System.out.println("Cell with " + entry.getId() + " is updated to " + "FROM Y " + fromY + " TO Y " + toY);
                    this.update(columnType, entry.getId(), fromY, toY, direction);
                });
    }

    @Override
    public Optional<CellState> get(int id) {
        return currentCellsState.entrySet().stream().map(entrySet -> {
            return entrySet.getValue();
        }).map(entry -> entry.get(id)).findAny();
    }

    @Override
    public List<CellState> getChangeCellStates() {
        return currentCellsState.entrySet().stream().flatMap(entry -> {
            return entry.getValue().entrySet().stream().map(ent -> ent.getValue()).filter(CellState::isChangeable);
        }).collect(Collectors.toList());
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
