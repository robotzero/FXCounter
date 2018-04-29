package com.robotzero.counter.event.result;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import io.reactivex.Observable;

public class ScrollResult implements Result {
    private Observable<Direction> direction;
    private ColumnType columnType;

    public ScrollResult(Observable<Direction> direction, ColumnType columnType) {
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
