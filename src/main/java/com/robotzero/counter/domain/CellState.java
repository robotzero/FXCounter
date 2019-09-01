package com.robotzero.counter.domain;

import java.util.Optional;

public class CellState {

    private final Location currentLocation;
    private final Location previousLocation;
    private final DirectionType currentDirection;
    private final DirectionType previousDirection;
    private final ColumnType columnType;
    private final int id;
    private int timerValue;
    private final CellStatePosition cellStatePosition;

    public CellState(
            final int id,
            Location currentLocation,
            Location previousLocation,
            DirectionType currentDirection,
            DirectionType previousDirection,
            ColumnType columnType,
            CellStatePosition cellStatePosition
    ) {
        this.id = id;
        this.timerValue = -1;
        this.previousLocation = previousLocation;
        this.currentLocation = currentLocation;
        this.columnType = columnType;
        this.previousDirection = previousDirection;
        this.currentDirection = currentDirection;
        this.cellStatePosition = cellStatePosition;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public int getId() {
        return id;
    }

    public DirectionType getCurrentDirection() {
        return currentDirection;
    }

    public DirectionType getPreviousDirection() {
        return previousDirection;
    }

    public CellState createNew(Location location, DirectionType currentDirection, DirectionType previousDirection, CellStatePosition cellStatePosition) {
        return new CellState(this.id, location, this.currentLocation,  currentDirection, previousDirection, this.columnType, cellStatePosition);
    }

    public CellState withTimerValue(final int timerValue){
        this.timerValue = timerValue;
        return this;
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

    public CellStatePosition getCellStatePosition() {
        return cellStatePosition;
    }

    public int getTimerValue() {
        return Optional.of(this.timerValue).filter(timerValue -> timerValue != -1).orElseThrow(() -> new RuntimeException());
    }

    public class Id {
        private final int id;
        public Id() {
            this.id = CellState.this.id;
        }
    }
}

