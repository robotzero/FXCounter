package com.robotzero.counter.event;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;

public final class ScrollEvent implements SubmitEvent {
    private Direction direction;
    private ColumnType columnType;

    public ScrollEvent(Direction direction, ColumnType columnType) {
        this.direction = direction;
        this.columnType = columnType;
    }
}
