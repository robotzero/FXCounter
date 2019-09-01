package com.robotzero.counter.domain;

public class Direction {

    private final ColumnType columnType;
    private final DirectionType directionType;

    public Direction(final ColumnType columnType, final DirectionType directionType) {
        this.columnType = columnType;
        this.directionType = directionType;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public DirectionType getDirectionType() {
        return directionType;
    }
}
