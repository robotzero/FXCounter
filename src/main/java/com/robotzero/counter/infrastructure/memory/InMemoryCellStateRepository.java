package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.*;
import java.util.*;
import java.util.function.Function;

public class InMemoryCellStateRepository implements CellStateRepository {
    private Map<ColumnType, Deque<CellState>> currentCellsState;

    @Override
    public void initialize(Map<ColumnType, Deque<CellState>> currentCellsState) {
        this.currentCellsState = currentCellsState;
    }

    @Override
    public void save(ColumnType columnType, Deque<CellState> newCellState) {
        this.currentCellsState.put(columnType, newCellState);
    }

    @Override
    public CellState get(ColumnType columnType, Function<Deque<CellState>, CellState> retriever) {
        return retriever.apply(this.currentCellsState.get(columnType));
    }

    @Override
    public Optional<CellState> get(int id) {
        return currentCellsState.values().stream().flatMap(Collection::stream).filter(cellState -> cellState.getId() == id).findFirst();
    }

    @Override
    public Deque<CellState> getAll(ColumnType columnType) {
        return this.currentCellsState.get(columnType);
    }

    @Override
    public String toString() {
        return "InMemoryCellStateRepository{" +
                "currentCellsState=" + currentCellsState +
                '}';
    }
}
