package com.robotzero.counter.domain;

import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Cell {

    private VBox rectangle;
    private Text label;
    private TranslateTransition translateTransition;
    private IntegerProperty currentSize;
    private ColumnType columnType;

    public Cell(
            VBox rectangle,
            Text label,
            TranslateTransition translateTransition,
            IntegerProperty currentSize,
            ColumnType columnType
    ) {
        this.rectangle = rectangle;
        this.label = label;
        this.translateTransition = translateTransition;
        this.currentSize = currentSize;
        this.columnType = columnType;
    }

    public void animate(CellState cellState, Duration duration) {
        if (cellState.getId() == Integer.parseInt(rectangle.getId())) {
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
        return Integer.parseInt(this.rectangle.getId());
    }

    @Override
    public String toString() {
        return "Cell{" +
                "rectangle=" + rectangle.getTranslateY() +
                "obj=" + this.rectangle +
                ", label=" + label +
                '}';
    }
}
