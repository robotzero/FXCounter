package com.robotzero.counter.domain;

public class CellState {

    private final Location currentLocation;
    private final Location previousLocation;
    private final Direction currentDirection;
    private final Direction previousDirection;
    private final ColumnType columnType;
    private final int id;

    public CellState(int id, Location currentLocation, Location previousLocation, Direction currentDirection, Direction previousDirection, ColumnType columnType) {
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

    public Location getPreviousLocation() {
        return previousLocation;
    }

    public boolean isChangeable() {
        if (columnType == ColumnType.SECONDS) {
                System.out.println("==========");
                System.out.println(this);
                System.out.println(currentLocation.getFromY());
                System.out.println(getCurrentDirection().getDirectionType());
                System.out.println("==========");
        }

        if (getCurrentDirection().getDirectionType() == DirectionType.VOID) {
            return currentLocation.getFromY() == -90;
        }

        if (getCurrentDirection().getDirectionType() == DirectionType.UP || getCurrentDirection().getDirectionType() == DirectionType.STARTUP) {
            return currentLocation.getFromY() == 270;
        }

        if (getCurrentDirection().getDirectionType() == DirectionType.STARTDOWN) {
            return currentLocation.getFromY() == 180;
        }

        if (getCurrentDirection().getDirectionType() == DirectionType.DOWN && getPreviousDirection().getDirectionType() == DirectionType.STARTDOWN) {
            return currentLocation.getFromY() == 180;
        }

        if (getCurrentDirection().getDirectionType() == DirectionType.DOWN && getPreviousDirection().getDirectionType() != DirectionType.STARTDOWN) {
            return currentLocation.getFromY() == -90;
        }

        if (getCurrentDirection().getDirectionType() == DirectionType.SWITCHDOWN && getPreviousDirection().getDirectionType() == DirectionType.UP){
            return currentLocation.getFromY() == 180;
        }

        return false;

//        return newLocation.getFromY() == -90 || newLocation.getFromY() == 270;
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
        return new CellState(this.id, new Location(fromY, toY), this.currentLocation,  newDirection, this.getPreviousDirection(), this.columnType);
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
