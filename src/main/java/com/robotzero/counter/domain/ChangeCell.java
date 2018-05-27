package com.robotzero.counter.domain;

public class ChangeCell {

    private Cell cell;
    private double translateY;

    public ChangeCell(Cell cell, double translateY) {
        this.cell = cell;
        this.translateY = translateY;
    }

    public Cell getCell() {
        return this.cell;
    }

    public double getTranslateY() {
        return translateY;
    }
}
