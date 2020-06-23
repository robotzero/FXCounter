package com.robotzero.counter.domain;

import java.util.Map;

public class ColumnState {
  private final Map<Integer, CellState> cellStates;

  public ColumnState(final Map<Integer, CellState> cellStates) {
    this.cellStates = cellStates;
  }

  public Map<Integer, CellState> getCellStates() {
    return cellStates;
  }

  public void save(CellState cellState) {
    cellStates.computeIfPresent(
      cellState.getId(),
      (id, currentCellState) -> {
        return cellState;
      }
    );
  }
}
