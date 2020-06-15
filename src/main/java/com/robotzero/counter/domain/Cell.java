package com.robotzero.counter.domain;

import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Cell {

    private Integer rectangleId;
    private Text label;
    private TranslateTransition translateTransition;
    private IntegerProperty currentSize;
    private ColumnType columnType;
    private CellState cellState;

    public Cell(
            Integer rectangleId,
            Text label,
            TranslateTransition translateTransition,
            IntegerProperty currentSize,
            ColumnType columnType,
            CellState cellState
    ) {
        this.rectangleId = rectangleId;
        this.label = label;
        this.translateTransition = translateTransition;
        this.currentSize = currentSize;
        this.columnType = columnType;
        this.cellState = cellState;
    }

    public void animate(CellState cellState, Duration duration) {
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

    public ColumnType getColumnType() {
        return columnType;
    }

    public int getId() {
        return rectangleId;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "rectangleId=" + rectangleId +
                ", label=" + label +
                '}';
    }
}
