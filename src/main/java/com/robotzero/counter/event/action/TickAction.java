package com.robotzero.counter.event.action;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.TimerType;

public class TickAction implements Action {

    private double delta;
    private final ColumnType columnType;
    private final TimerType timerType;

    public TickAction(double delta, ColumnType columnType, TimerType timerType) {
        this.delta = delta;
        this.columnType = columnType;
        this.timerType = timerType;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public TimerType getTimerType() {
        return timerType;
    }

    public double getDelta() {
        return delta;
    }
}
