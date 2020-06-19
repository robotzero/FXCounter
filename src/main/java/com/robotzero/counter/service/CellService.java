package com.robotzero.counter.service;

import com.robotzero.counter.domain.CellStateRepository;
import com.robotzero.counter.domain.ColumnState;
import com.robotzero.counter.domain.ColumnType;
import java.util.Map;

public class CellService {
  private final CellStateRepository cellStateRepository;

  public CellService(final CellStateRepository cellStateRepository) {
    this.cellStateRepository = cellStateRepository;
  }

  public void initialize(final Map<ColumnType, ColumnState> cellStates) {
    this.cellStateRepository.initialize(cellStates);
  }
}
