package com.robotzero.counter.domain;

import java.util.Map;

public interface CellStateRepository {
  void initialize(final Map<ColumnType, ColumnState> columnStates);
  CellState getById(final ColumnType columnType, final int cellStateId);
  ColumnState getColumn(final ColumnType columnType);
}
