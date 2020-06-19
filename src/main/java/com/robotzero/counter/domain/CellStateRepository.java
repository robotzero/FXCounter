package com.robotzero.counter.domain;

import com.robotzero.counter.view.Column;
import java.util.Map;

public interface CellStateRepository {
  void initialize(Map<Integer, CellState> cellStates);

  //    CellState get(int id);

  CellState getById(int cellStateId);
}
