package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.ColumnType;

import java.util.List;
import java.util.Map;

public class CurrentClockState {
    private final List<CellState> cellStates;
    private final Map<ColumnType, Integer> timerStateForCells;

    public CurrentClockState(final Map<ColumnType, Integer> timerStateForCells, final List<CellState> cellStates) {
        this.timerStateForCells = timerStateForCells;
        this.cellStates = cellStates;
    }

    public List<CellState> getCellStates() {
        return cellStates;
    }

    public int getLabelForColumn(ColumnType columnType) {
        return this.timerStateForCells.get(columnType);
    }
}
