package com.robotzero.counter.service;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.CellStateRepository;
import java.util.Map;

public class CellService {
  private final CellStateRepository cellStateRepository;

  public CellService(final CellStateRepository cellStateRepository) {
    this.cellStateRepository = cellStateRepository;
  }

  public void initialize(final Map<Integer, CellState> cellStates) {
    this.cellStateRepository.initialize(cellStates);
  }
  //    public CellState get(final int id) {
  //        return this.cellStateRepository.get(id);
  //    }

  //  public Column getColumn(final ColumnType columnType) {
  //    return this.cellStateRepository.getColumn(columnType);
  //  }
}
