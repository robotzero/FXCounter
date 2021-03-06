package com.robotzero.counter.view;

import com.robotzero.counter.domain.CellState;
import java.util.Map;
import javafx.util.Duration;

public class Column {
  private final Map<Integer, Cell> cells;

  public Column(Map<Integer, Cell> cells) {
    this.cells = cells;
  }

  public void play(Duration duration, CellState cellState) {
    this.cells.get(cellState.getId()).animate(duration, cellState);
  }

  public void setLabels(int index, Integer value) {
    this.cells.get(index).setLabel(value);
  }

  public void setLabel(int id, Integer value) {
    Cell cell = this.cells.get(id);
    cell.setLabel(value);
  }

  public Map<Integer, Cell> getCells() {
    return this.cells;
  }
}
