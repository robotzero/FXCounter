package com.robotzero.counter.domain;

import java.util.List;
import java.util.Map;

public interface CellStateRepository {
  void initialize(
    Map<ColumnType, List<CellState>> currentCellsState,
    Map<ColumnType, Column> timerColumns
  );

  //    CellState get(int id);

  Column getColumn(ColumnType columnType);
  //    void save(ColumnType columnType, List<CellState> updatedCellState);

  //    CellState get(ColumnType columnType, Function<Deque<CellState>, CellState> retriever);
}
