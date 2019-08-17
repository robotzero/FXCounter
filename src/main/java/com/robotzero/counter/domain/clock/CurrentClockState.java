package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.ColumnType;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class CurrentClockState {
    private final List<CellState> cellStates;
    private final LocalTime mainClockState;
    private final Map<ColumnType, Integer> timerStateForCells;

    public CurrentClockState(final Map<ColumnType, Integer> timerStateForCells, final List<CellState> cellStates, final LocalTime mainClockState) {
        this.timerStateForCells = timerStateForCells;
        this.cellStates = cellStates;
        this.mainClockState = mainClockState;
    }

    public List<CellState> getCellStates() {
        return cellStates;
    }

    public int getLabelForColumn(ColumnType columnType) {
        return this.timerStateForCells.get(columnType);
    }

    public LocalTime getMainClockState() {
        return mainClockState;
    }

    public Map<ColumnType, Integer> getTimerStateForCells() {
        return timerStateForCells;
    }
}
