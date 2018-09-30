package com.robotzero.counter.event.result;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.TimerType;
import com.robotzero.counter.domain.clock.CurrentClockState;
import javafx.util.Duration;

public class TickResult implements Result {
    private final CurrentClockState currentClockState;
    private final ColumnType columnType;
    private final TimerType timerType;

    public TickResult(CurrentClockState currentClockState, ColumnType columnType, TimerType timerType) {
        this.currentClockState = currentClockState;
        this.columnType = columnType;
        this.timerType = timerType;
    }

    public CurrentClockState getLabels() {
        return this.currentClockState;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public TimerType getTimerType() {
        return timerType;
    }

    public Duration getDuration() {
        if (timerType.equals(TimerType.SCROLL)) {
            return Duration.millis(200);
        }

        if (timerType.equals(TimerType.TICK)) {
            return Duration.millis(600);
        }

        return Duration.millis(10);
    }

    @Override
    public String toString() {
        return "TickResult{" +
                ", currentClockState=" + currentClockState.toString() +
                ", columnType=" + columnType +
                ", timerType=" + timerType +
                '}';
    }
}
