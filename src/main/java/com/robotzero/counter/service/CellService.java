package com.robotzero.counter.service;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.CellStatePosition;
import com.robotzero.counter.domain.CellStateRepository;
import com.robotzero.counter.domain.ColumnState;
import com.robotzero.counter.domain.ColumnType;
import java.util.Map;
import java.util.Optional;

public class CellService {
  private final CellStateRepository cellStateRepository;

  public CellService(final CellStateRepository cellStateRepository) {
    this.cellStateRepository = cellStateRepository;
  }

  public void initialize(final Map<ColumnType, ColumnState> cellStates) {
    this.cellStateRepository.initialize(cellStates);
  }

  public Optional<CellState> getAllNonChangeable(final ColumnType columnType) {
    return cellStateRepository
      .getColumn(columnType)
      .getCellStates()
      .entrySet()
      .stream()
      .filter(a -> a.getValue().getCellStatePosition() != CellStatePosition.CHANGEABLE)
      .map(a -> a.getValue())
      .findFirst();
  }
}
