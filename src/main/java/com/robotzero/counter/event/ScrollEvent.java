package com.robotzero.counter.event;

import com.robotzero.counter.domain.ColumnType;

public final class ScrollEvent implements MainViewEvent {

    private String parentNodeId;
    private double delta;

    public ScrollEvent(String parentNodeId, double delta) {
        this.parentNodeId = parentNodeId;
        this.delta = delta;
    }

    public double getDelta() {
        return delta;
    }

    public ColumnType getColumnType() {
        return ColumnType.valueOf(parentNodeId.toUpperCase());
    }
}
