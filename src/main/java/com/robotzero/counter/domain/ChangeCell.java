package com.robotzero.counter.domain;

import javafx.scene.text.Text;

public class ChangeCell {

    private Text label;
    private double translateY;
    private ColumnType columnType;

    public ChangeCell(Text label, double translateY, ColumnType columnType) {
        this.label = label;
        this.translateY = translateY;
        this.columnType = columnType;
    }

    public Text getLabel() {
        return label;
    }

    public double getTranslateY() {
        return translateY;
    }

    public ColumnType getColumnType() {
        return columnType;
    }
}
