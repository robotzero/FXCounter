package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.CellState;

import java.util.List;

public class CurrentClockState {
    private final List<CellState> cellStates;

    public CurrentClockState(final List<CellState> cellStates) {
        this.cellStates = cellStates;
    }

    public List<CellState> getCellStates() {
        return cellStates;
    }
}
