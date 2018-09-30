package com.robotzero.counter.domain;

public class CellState {

    private final Location newLocation;
    private final Location previousLocation;
    private final Direction currentDirection;
    private final Direction previousDirection;
    private final ColumnType columnType;
    private final int id;

    public CellState(int id, Location newLocation, Location previousLocation, Direction currentDirection, Direction previousDirection, ColumnType columnType) {
        this.id = id;
        this.previousLocation = previousLocation;
        this.newLocation = newLocation;
        this.columnType = columnType;
        this.previousDirection = previousDirection;
        this.currentDirection = currentDirection;
    }

    public Location getNewLocation() {
        return newLocation;
    }

    public Location getPreviousLocation() {
        return previousLocation;
    }

    public boolean isChangeable() {
        return newLocation.getFromY() == 90 || newLocation.getFromY() == 270;
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

    public CellState createNew(double fromY, double toY, Direction newDirection) {
        return new CellState(this.id, new Location(fromY, toY), this.getNewLocation(),  newDirection, this.getPreviousDirection(), this.columnType);
    }

    @Override
    public String toString() {
        return "CellState{" +
                "newLocation=" + newLocation +
                "previousLocation" + previousLocation +
                "previousDirection=" + previousDirection +
                "currentDirection=" + currentDirection +
                ", columnType=" + columnType +
                ", id=" + id +
                '}';
    }
}
