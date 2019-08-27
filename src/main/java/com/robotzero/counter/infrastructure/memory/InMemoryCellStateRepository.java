package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.*;
import java.util.*;

public class InMemoryCellStateRepository implements CellStateRepository {
    private Map<ColumnType, List<CellState>> currentCellsState;

    @Override
    public void initialize(Map<ColumnType, List<CellState>> currentCellsState) {
        this.currentCellsState = currentCellsState;
    }

    @Override
    public void save(ColumnType columnType, List<CellState> newCellState) {
        this.currentCellsState.put(columnType, newCellState);
    }

    @Override
    public CellState get(int id) {
        return currentCellsState.values().stream().flatMap(Collection::stream).filter(cellState -> cellState.getId() == id).findFirst().orElseThrow(() -> new RuntimeException("Invalid cell state id"));
    }

    @Override
    public List<CellState> getAll(ColumnType columnType) {
        return this.currentCellsState.get(columnType);
    }

    @Override
    public String toString() {
        return "InMemoryCellStateRepository{" +
                "currentCellsState=" + currentCellsState +
                '}';
    }
}
