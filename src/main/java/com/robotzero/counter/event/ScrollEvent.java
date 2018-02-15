package com.robotzero.counter.event;

import com.robotzero.counter.domain.ColumnType;

public final class ScrollEvent implements MainViewEvent {
    private ColumnType columnType;
    private double delta;

    public ScrollEvent(ColumnType columnType, double delta) {
        this.delta = delta;
        this.columnType = columnType;
    }

    public double getDelta() {
        return delta;
    }

    public ColumnType getColumnType() {
        return columnType;
    }
}
