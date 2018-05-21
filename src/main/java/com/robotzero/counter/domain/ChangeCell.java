package com.robotzero.counter.domain;

public class ChangeCell {

    private Cell cell;
    private Direction direction;

    public ChangeCell(Cell cell, Direction direction) {
        this.cell = cell;
        this.direction = direction;
    }

    public Cell getCell() {
        return this.cell;
    }

    public Direction getDirection() {
        return this.direction;
    }
}
