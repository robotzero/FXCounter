package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.*;
import java.util.*;

public class InMemoryCellStateRepository implements CellStateRepository {
    private Map<ColumnType, List<CellState>> currentCellsState;
    private Map<ColumnType, Column> timerColumns;

    @Override
    public void initialize(Map<ColumnType, List<CellState>> currentCellsState, Map<ColumnType, Column> timerColumns) {
        this.currentCellsState = currentCellsState;
        this.timerColumns = timerColumns;
    }

    @Override
    public void save(ColumnType columnType, List<CellState> newCellState) {
        this.currentCellsState.put(columnType, newCellState);
    }

//    @Override
//    public CellState get(int id) {
//        return currentCellsState.values().stream().flatMap(Collection::stream).filter(cellState -> cellState.getId() == id).findFirst().orElseThrow(() -> new RuntimeException("Invalid cell state id"));
//    }

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
