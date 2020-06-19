package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.CellStateRepository;
import java.util.Map;

public class InMemoryCellStateRepository implements CellStateRepository {
  private Map<Integer, CellState> cellStates;

  @Override
  public void initialize(Map<Integer, CellState> cellStates) {
    this.cellStates = cellStates;
  }

  @Override
  public CellState getById(final int cellStateId) {
    return this.cellStates.get(cellStateId);
  }

  @Override
  public String toString() {
    return ("InMemoryCellStateRepository{" + "currentCellsState=" + this.cellStates + '}');
  }
}
