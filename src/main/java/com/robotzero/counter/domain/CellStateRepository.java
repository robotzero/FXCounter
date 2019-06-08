package com.robotzero.counter.domain;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface CellStateRepository {

    void initialize(Map<ColumnType, ArrayDeque<CellState>> currentCellsState);

    CellState getChangeable(ColumnType columnType);

    Optional<CellState> get(int id);

    ArrayDeque<CellState> getAll(ColumnType columnType);

    void save(ColumnType columnType, ArrayDeque<CellState> updatedCellState);

    CellState get(ColumnType columnType, Function<ArrayDeque<CellState>, CellState> retriever);
}
