package com.robotzero.counter.event.action;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;

public class ScrollAction implements Action {
    private Direction direction;
    private ColumnType columnType;

    public ScrollAction(Direction direction, ColumnType columnType) {
        this.direction = direction;
        this.columnType = columnType;
    }

    public Direction getDirection() {
        return direction;
    }

    public ColumnType getColumnType() {
        return columnType;
    }
}
