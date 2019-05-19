package com.robotzero.counter.domain;

public class CellState {

    private final Location currentLocation;
    private final Location previousLocation;
    private final DirectionType currentDirection;
    private final DirectionType previousDirection;
    private final ColumnType columnType;
    private final int id;

    public CellState(int id, Location currentLocation, Location previousLocation, DirectionType currentDirection, DirectionType previousDirection, ColumnType columnType) {
        this.id = id;
        this.previousLocation = previousLocation;
        this.currentLocation = currentLocation;
        this.columnType = columnType;
        this.previousDirection = previousDirection;
        this.currentDirection = currentDirection;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public boolean isChangeable() {
        if (columnType == ColumnType.SECONDS) {
                System.out.println("==========");
                System.out.println("From y " + currentLocation.getFromY());
                System.out.println("To y " + currentLocation.getToY());
                System.out.println("Dir type" + currentDirection);
                System.out.println("Prev dir type " + previousDirection);
                System.out.println("Prev from y " + previousLocation.getFromY());
                System.out.println("Prev to y " + previousLocation.getToY());
                System.out.println("==========");
        }

        if (this.currentDirection == DirectionType.STARTUP) {
            return currentLocation.getFromY() == 270;
        }

        if (this.currentDirection == DirectionType.VOID) {
            return currentLocation.getFromY() == 270 || currentLocation.getFromY() == -90;
        }

        if (this.currentDirection == DirectionType.UP) {
            return currentLocation.getFromY() == 270  || currentLocation.getFromY() == -90;
        }

        if (this.currentDirection == DirectionType.DOWN) {
            return currentLocation.getFromY() == -90 || currentLocation.getFromY() == 270;
        }

        if (this.currentDirection == DirectionType.SWITCHUP) {
            return currentLocation.getFromY() == 270;
        }

        if (this.currentDirection == DirectionType.STARTDOWN) {
            return currentLocation.getFromY() == -90;
        }

        if (this.currentDirection == DirectionType.SWITCHDOWN) {
            return currentLocation.getFromY() == -90;
        }

        return false;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public int getId() {
        return id;
    }

    public CellState createNew(double fromY, double toY, DirectionType currentDirection, DirectionType previousDirection) {
        return new CellState(this.id, new Location(fromY, toY), this.currentLocation,  currentDirection, previousDirection, this.columnType);
    }

    @Override
    public String toString() {
        return "CellState{" +
                "currentLocation=" + currentLocation +
                "previousLocation" + previousLocation +
                "previousDirection=" + previousDirection +
                "currentDirection=" + currentDirection +
                ", columnType=" + columnType +
                ", id=" + id +
                '}';
    }
}
