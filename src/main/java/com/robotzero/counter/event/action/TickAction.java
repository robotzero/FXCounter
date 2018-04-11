package com.robotzero.counter.event.action;

import com.robotzero.counter.domain.Cell;

import java.util.Optional;

public class TickAction {

    private final Integer label;
    private final Optional<Cell> cell;

    public TickAction(Optional<Cell> cell, Integer label) {
        this.cell = cell;
        this.label = label;
    }

    public Integer getLabel() {
        return label;
    }

    public Optional<Cell> getCell() {
        return cell;
    }
}
