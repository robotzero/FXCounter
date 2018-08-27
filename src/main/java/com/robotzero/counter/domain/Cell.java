package com.robotzero.counter.domain;

import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Cell {

    private VBox rectangle;
    private Location location;
    private Text label;
    private TranslateTransition translateTransition;
    private IntegerProperty currentSize;
    private ColumnType columnType;

    public Cell(
            VBox rectangle,
            Location location,
            Text label,
            TranslateTransition translateTransition,
            IntegerProperty currentSize,
            ColumnType columnType
    ) {
        this.rectangle = rectangle;
        this.location = location;
        this.label = label;
        this.translateTransition = translateTransition;
        this.currentSize = currentSize;
        this.columnType = columnType;
    }

    public void animate(Direction direction, Duration duration) {
        double fromY = location.calculateFromY(currentSize, direction.getDelta(), rectangle.getTranslateY());
        double toY = location.calculateToY(currentSize, direction.getDelta(), rectangle.getTranslateY());
        translateTransition.setDuration(duration);
        translateTransition.setFromY(fromY);
        translateTransition.setToY(toY);
        translateTransition.play();
    }

    //@TOOD the returned cell depends on the delta/direction.
    public ChangeCell getChangeCell() {
        if (rectangle.translateYProperty().get() == -90 || rectangle.translateYProperty().get() == 270) {
            return new ChangeCell(this.label, rectangle.getTranslateY(), this.columnType);
        }

        return new ChangeCell(null, 0, ColumnType.VOID);
    }

    public void setLabel(int newLabel) {
        if (!this.label.textProperty().getValue().equals(String.format("%02d", newLabel))) {
            this.label.textProperty().setValue(String.format("%02d", newLabel));
        }
    }

    public ColumnType getColumnType() {
        return columnType;
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
