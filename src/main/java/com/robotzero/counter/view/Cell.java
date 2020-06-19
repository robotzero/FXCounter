package com.robotzero.counter.view;

import com.robotzero.counter.domain.CellState;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Cell {
  private Integer rectangleId;
  private Text label;
  private TranslateTransition translateTransition;
  private IntegerProperty currentSize;

  public Cell(Integer rectangleId, Text label, TranslateTransition translateTransition, IntegerProperty currentSize) {
    this.rectangleId = rectangleId;
    this.label = label;
    this.translateTransition = translateTransition;
    this.currentSize = currentSize;
  }

  public void animate(Duration duration, CellState cellState) {
    if (cellState.getId() == rectangleId) {
      translateTransition.setDuration(duration);
      translateTransition.setFromY(cellState.getCurrentLocation().getFromY());
      translateTransition.setToY(cellState.getCurrentLocation().getToY());
      translateTransition.play();
    }
  }

  public void setLabel(int newLabel) {
    if (!this.label.textProperty().getValue().equals(String.format("%02d", newLabel))) {
      this.label.textProperty().setValue(String.format("%02d", newLabel));
    }
  }

  public int getId() {
    return rectangleId;
  }

  @Override
  public String toString() {
    return "Cell{" + "rectangleId=" + rectangleId + ", label=" + label + '}';
  }
}
