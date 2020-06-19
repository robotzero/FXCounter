package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.CellStateRepository;
import com.robotzero.counter.domain.ColumnState;
import com.robotzero.counter.domain.ColumnType;
import java.util.Map;

public class InMemoryCellStateRepository implements CellStateRepository {
  private Map<ColumnType, ColumnState> cellStates;

  @Override
  public void initialize(Map<ColumnType, ColumnState> cellStates) {
    this.cellStates = cellStates;
  }

  @Override
  public CellState getById(final ColumnType columnType, final int cellStateId) {
    return this.cellStates.get(columnType).getCellStates().get(cellStateId);
  }

  @Override
  public ColumnState getColumn(ColumnType columnType) {
    return cellStates.get(columnType);
  }

  @Override
  public String toString() {
    return ("InMemoryCellStateRepository{" + "currentCellsState=" + this.cellStates + '}');
  }
}
