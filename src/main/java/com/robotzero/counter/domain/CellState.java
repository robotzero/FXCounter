package com.robotzero.counter.domain;

public class CellState {

    private final double currentPosition;
    private final double previousPosition;
    private final ColumnType columnType;
    private final Direction currentDirection;
    private final Direction previousDirection;
    private final int id;

    public CellState(int id, double currentPosition, double previousPosition, Direction currentDirection, Direction previousDirection, ColumnType columnType) {
        this.id = id;
        this.currentPosition = currentPosition;
        this.previousPosition = previousPosition;
        this.columnType = columnType;
        this.previousDirection = previousDirection;
        this.currentDirection = currentDirection;
    }

    public double getCurrentPosition() {
        return currentPosition;
    }

    public boolean isChangeable() {
        return currentPosition == 90 || currentPosition == 270;
    }

    public double getPreviousPosition() {
        return previousPosition;
    }

    public Direction getCurrentDirection() {
        return currentDirection == null ? new Direction(columnType, DirectionType.VOID) : currentDirection;
    }

    public Direction getPreviousDirection() {
        return previousDirection == null ? new Direction(columnType, DirectionType.VOID) : previousDirection;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public int getId() {
        return id;
    }

    public CellState createNew(double newPosition, double previousPosition, Direction newDirection) {
        return new CellState(this.id, newPosition, previousPosition, newDirection, this.getPreviousDirection(), this.columnType);
    }

    @Override
    public String toString() {
        return "CellState{" +
                "currentPosition=" + currentPosition +
                "previousPosition=" + previousPosition +
                "previousDirection=" + previousDirection +
                "currentDirection=" + currentDirection +
                ", columnType=" + columnType +
                ", id=" + id +
                '}';
    }
}
