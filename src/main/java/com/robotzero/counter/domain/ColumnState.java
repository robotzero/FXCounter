package com.robotzero.counter.domain;

import java.util.Map;

public class ColumnState {
  private Map<Integer, CellState> cellStates;

  public void startCellStates() {
    this.cellStates = ColumnStateFactory.build();
  }

  public Map<Integer, CellState> getCellStates() {
    return cellStates;
  }
}
