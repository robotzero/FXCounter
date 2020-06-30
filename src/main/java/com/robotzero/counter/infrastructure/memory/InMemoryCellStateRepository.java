package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.CellStateRepository;
import com.robotzero.counter.domain.ColumnState;
import com.robotzero.counter.domain.ColumnType;
import java.util.Map;
import java.util.Optional;

public class InMemoryCellStateRepository implements CellStateRepository {
  private Map<ColumnType, ColumnState> cellStates;

  @Override
  public void initialize(final Map<ColumnType, ColumnState> cellStates) {
    this.cellStates = cellStates;
  }

  @Override
  public CellState getById(final ColumnType columnType, final int cellStateId) {
    return this.cellStates.get(columnType).getCellStates().get(cellStateId);
  }

  @Override
  public ColumnState getColumn(final ColumnType columnType) {
    return Optional
      .ofNullable(cellStates)
      .map(
        cellStates -> {
          return cellStates.get(columnType);
        }
      )
      .orElseGet(() -> new ColumnState(Map.of()));
  }

  @Override
  public void save(final ColumnType columnType, final CellState cellState) {
    this.cellStates.get(columnType).save(cellState);
  }

  @Override
  public String toString() {
    return ("InMemoryCellStateRepository{" + "currentCellsState=" + this.cellStates + '}');
  }
}
