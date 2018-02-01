package com.robotzero.counter.event.action;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import io.reactivex.Observable;

public class ScrollAction implements Action {
    private Observable<Direction> direction;
    private ColumnType columnType;

    public ScrollAction(Observable<Direction> direction, ColumnType columnType) {
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
