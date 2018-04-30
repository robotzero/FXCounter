package com.robotzero.counter.event.action;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import io.reactivex.Observable;

public class TickAction implements Action {

    private final Observable<Direction> direction;
    private final ColumnType columnType;

    public TickAction(Observable<Direction> direction, ColumnType columnType) {
        this.direction = direction;
        this.columnType = columnType;
    }

    public Observable<Direction> getDirection() {
        return direction;
    }

    public ColumnType getColumnType() {
        return columnType;
    }
}
