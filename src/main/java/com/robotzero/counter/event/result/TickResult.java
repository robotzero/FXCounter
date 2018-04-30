package com.robotzero.counter.event.result;

import com.robotzero.counter.domain.Cell;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.clock.CurrentClockState;
import io.reactivex.Observable;

public class TickResult implements Result {

    private CurrentClockState labels;
    private Cell secondsCell;
    private Cell minutesCell;
    private Cell hoursCell;
    private Direction direction;
    private ColumnType columnType;

    public TickResult(Cell secondsCell, Cell minutesCell, Cell hoursCell, Direction direction, ColumnType columnType) {
        this.secondsCell = secondsCell;
        this.minutesCell = minutesCell;
        this.hoursCell = hoursCell;
        this.direction = direction;
        this.columnType = columnType;
    }

    public TickResult withCurrentClockState(CurrentClockState currentClockState) {
        this.labels = currentClockState;
        return this;
    }

    public CurrentClockState getLabels() {
        return labels;
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

    public Direction getDirection() {
        return direction;
    }

    public ColumnType getColumnType() {
        return columnType;
    }
}
