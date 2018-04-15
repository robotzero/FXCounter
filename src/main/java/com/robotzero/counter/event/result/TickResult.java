package com.robotzero.counter.event.result;

import com.robotzero.counter.domain.Cell;
import com.robotzero.counter.domain.clock.CurrentClockState;

public class TickResult implements Result {

    private CurrentClockState labels;
    private Cell secondsCell;
    private Cell minutesCell;
    private Cell hoursCell;

    public TickResult(Cell secondsCell, Cell minutesCell, Cell hoursCell) {
        this.secondsCell = secondsCell;
        this.minutesCell = minutesCell;
        this.hoursCell = hoursCell;
    }

    public TickResult withCurrentClockState(CurrentClockState currentClockState) {
        this.labels = currentClockState;
        return this;
    }
    public CurrentClockState getLabels() {
        return labels;
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
}
