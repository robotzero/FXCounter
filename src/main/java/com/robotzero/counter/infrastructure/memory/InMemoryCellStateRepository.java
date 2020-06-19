package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.*;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryCellStateRepository implements CellStateRepository {
  private Map<ColumnType, List<CellState>> currentCellsState;
  private Map<ColumnType, Column> timerColumns;

  @Override
  public void initialize(Map<ColumnType, List<CellState>> currentCellsState, Map<ColumnType, Column> timerColumns) {
    this.currentCellsState = currentCellsState;
    this.timerColumns = timerColumns;
  }

  //    @Override
  //    public void save(ColumnType columnType, List<CellState> newCellState) {
  //        this.currentCellsState.put(columnType, newCellState);
  //    }

  //    @Override
  //    public CellState get(int id) {
  //        return currentCellsState.values().stream().flatMap(Collection::stream).filter(cellState -> cellState.getId() == id).findFirst().orElseThrow(() -> new RuntimeException("Invalid cell state id"));
  //    }

  @Override
  public Column getColumn(ColumnType columnType) {
    //        return this.timerColumns.get(columnType).getCells().stream().map(cell -> cell.getCellState()).collect(Collectors.toList());
    return this.timerColumns.get(columnType);
  }

  @Override
  public String toString() {
    return ("InMemoryCellStateRepository{" + "currentCellsState=" + currentCellsState + '}');
  }
}
