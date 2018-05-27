package com.robotzero.counter.event.result;

import com.robotzero.counter.domain.Cell;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.TimerType;
import com.robotzero.counter.domain.clock.CurrentClockState;

public class TickResult implements Result {
    private final Cell secondsCell;
    private final Cell minutesCell;
    private final Cell hoursCell;
    private final CurrentClockState currentClockState;
    private final ColumnType columnType;
    private final TimerType timerType;

    public TickResult(Cell secondsCell, Cell minutesCell, Cell hoursCell, CurrentClockState currentClockState, ColumnType columnType, TimerType timerType) {
        this.secondsCell = secondsCell;
        this.minutesCell = minutesCell;
        this.hoursCell = hoursCell;
        this.currentClockState = currentClockState;
        this.columnType = columnType;
        this.timerType = timerType;
    }

    public CurrentClockState getLabels() {
        return this.currentClockState;
    }

    public Cell getCell(ColumnType columnType) {
        if (columnType.equals(ColumnType.SECONDS)) {
            return getSecondsCell();
        }

        if (columnType.equals(ColumnType.MINUTES)) {
            return getMinutesCell();
        }

        if (columnType.equals(ColumnType.HOURS)) {
            return getHoursCell();
        }

        throw new RuntimeException("Unsupported column type.");
    }

    public Cell getSecondsCell() {
        return secondsCell;
    }

    public Cell getMinutesCell() {
        return minutesCell;
    }

    public Cell getHoursCell() {
        return hoursCell;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public TimerType getTimerType() {
        return timerType;
    }
}
