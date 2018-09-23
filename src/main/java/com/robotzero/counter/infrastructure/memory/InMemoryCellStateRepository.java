package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.*;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryCellStateRepository implements CellStateRepository {
    private Map<ColumnType, List<CellState>> currentCellsState;

    @Override
    public void initialize(Map<ColumnType, List<CellState>> currentCellsState) {
        this.currentCellsState = currentCellsState;
    }

    @Override
    public void update(ColumnType columnType, int id, double position, double oldPosition, Direction direction) {
        Optional<CellState> cellStateToUpdate = currentCellsState.get(columnType).stream().filter(cellState -> cellState.getId() == id).findFirst();
        cellStateToUpdate.ifPresent(cellState -> {
            CellState newCellState = cellState.createNew(position, oldPosition, direction);
            List<CellState> currentList = currentCellsState.get(columnType);
            currentList.set(index, newCellState);
        });
    }

    @Override
    public void update(Location location, Direction direction, ColumnType columnType) {
        currentCellsState.entrySet().stream().filter(entry -> entry.getKey() == columnType)
                .flatMap(entry -> {
                    return entry.getValue().stream();
                })
                .forEach(entry -> {
                    double fromY = location.calculateFromY(new SimpleIntegerProperty(90), direction.getDirectionType().getDelta(), entry.getCurrentPosition());
                    double toY = location.calculateToY(new SimpleIntegerProperty(90), direction.getDirectionType().getDelta(), entry.getCurrentPosition());
                    this.update(columnType, entry.getId(), fromY, toY, direction);
                });
    }

    @Override
    public List<CellState> getChangeCellStates() {
        return currentCellsState.entrySet().stream().flatMap(entry -> {
            return entry.getValue().stream().filter(CellState::isChangeable);
        }).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "InMemoryCellStateRepository{" +
                "currentCellsState=" + currentCellsState +
                '}';
    }
}
