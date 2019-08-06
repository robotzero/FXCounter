package com.robotzero.counter.domain;

import java.util.Deque;
import java.util.Map;
import java.util.function.Function;

public interface CellStateRepository {

    void initialize(Map<ColumnType, Deque<CellState>> currentCellsState);

    CellState get(int id);

    Deque<CellState> getAll(ColumnType columnType);

    void save(ColumnType columnType, Deque<CellState> updatedCellState);

    CellState get(ColumnType columnType, Function<Deque<CellState>, CellState> retriever);
}
