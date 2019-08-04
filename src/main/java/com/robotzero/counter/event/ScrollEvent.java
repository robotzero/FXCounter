package com.robotzero.counter.event;

import com.robotzero.counter.domain.ColumnType;

public final class ScrollEvent implements MainViewEvent {

    private String parentNodeId;
    private double delta;
    private int numOfScrolls;

    public ScrollEvent(String parentNodeId, double delta, int numOfScrolls) {
        this.parentNodeId = parentNodeId;
        this.delta = delta;
        this.numOfScrolls = numOfScrolls;
    }

    public double getDelta() {
        return delta;
    }

    public ColumnType getColumnType() {
        return ColumnType.valueOf(parentNodeId.toUpperCase());
    }

    public int getNumOfScrolls() {
        return numOfScrolls;
    }
}
