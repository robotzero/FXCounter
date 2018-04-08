package com.robotzero.counter.event.action;

import com.robotzero.counter.domain.Cell;

import java.util.Optional;

public class TickAction {

    private final Integer label;
    private final Optional<Cell> cell;

    public TickAction(Integer label, Optional<Cell> cell) {
        this.label = label;
        this.cell = cell;
    }

    public Integer getLabel() {
        return label;
    }

    public Optional<Cell> getCell() {
        return cell;
    }
}
