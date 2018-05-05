package com.robotzero.counter.event.action;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.TimerType;
import io.reactivex.Observable;

public class TickAction implements Action {

    private final Observable<Direction> direction;
    private final ColumnType columnType;
    private final TimerType timerType;

    public TickAction(Observable<Direction> direction, ColumnType columnType, TimerType timerType) {
        this.direction = direction;
        this.columnType = columnType;
        this.timerType = timerType;
    }

    public Observable<Direction> getDirection() {
        return direction;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public TimerType getTimerType() {
        return timerType;
    }
}
