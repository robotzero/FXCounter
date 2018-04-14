package com.robotzero.counter.event.result;

import com.robotzero.counter.domain.Cell;
import com.robotzero.counter.domain.clock.CurrentClockState;

public class TickResult implements Result {

    private final CurrentClockState labels;
    private final Cell cell;

    public TickResult(Cell cell, CurrentClockState labels) {
        this.cell = cell;
        this.labels = labels;
    }

    public CurrentClockState getLabels() {
        return labels;
    }

    public Cell getCell() {
        return cell;
    }
}
