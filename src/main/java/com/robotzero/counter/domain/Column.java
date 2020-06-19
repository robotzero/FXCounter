package com.robotzero.counter.domain;

import java.util.Map;
import javafx.util.Duration;

public class Column {
  private final Map<Integer, Cell> cells;

  public Column(Map<Integer, Cell> cells) {
    this.cells = cells;
  }

  public void play(Duration duration) {
    this.cells.forEach((key, value) -> value.animate(duration));
  }

  public void setLabels(int index, Integer value) {
    this.cells.get(index).setLabel(value);
  }

  public void setLabel(int id, Integer value) {
    Cell cell = this.cells.get(id);
    cell.setLabel(value);
    //        this.cells.entrySet().stream().filter(cell -> {
    //            return cell.getValue().getId() == id;
    //        }).findFirst().ifPresent(cell -> {
    //            cell.getValue().setLabel(value);
    //        });
  }

  public void setNewCellState() {}

  public Map<Integer, Cell> getCells() {
    return this.cells;
  }
}
