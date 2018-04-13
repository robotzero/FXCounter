package com.robotzero.counter.event.result;

import com.robotzero.counter.domain.Cell;

public class TickResult implements Result {

    private final Integer label;
    private final Cell cell;

    public TickResult(Cell cell, Integer label) {
        this.cell = cell;
        this.label = label;
    }

    public Integer getLabel() {
        return label;
    }

    public Cell getCell() {
        return cell;
    }
}
