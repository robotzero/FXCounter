package com.robotzero.counter.service;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.DirectionType;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

public class DirectionService {

    /* We have to track previous directions */
//    private Map<ColumnType, Map<Integer, DirectionType>> currentDir = new HashMap<>();
    private Map<ColumnType, DirectionType> currentDirection = new HashMap<>();
    private Map<ColumnType, DirectionType> previousDirection = new HashMap<>();

    @PostConstruct
    public void initialize() {
        currentDirection.put(ColumnType.SECONDS, DirectionType.VOID);
        previousDirection.put(ColumnType.SECONDS, DirectionType.VOID);
        currentDirection.put(ColumnType.MINUTES, DirectionType.VOID);
        previousDirection.put(ColumnType.MINUTES, DirectionType.VOID);
        currentDirection.put(ColumnType.HOURS, DirectionType.VOID);
        previousDirection.put(ColumnType.HOURS, DirectionType.VOID);
    }

    public Direction calculateDirection(CellState currentCellState, double delta) {
        Direction newdirection = null;
        DirectionType currentDirection = this.currentDirection.get(currentCellState.getColumnType());
        DirectionType previousDirection = this.previousDirection.get(currentCellState.getColumnType());
        if (delta < 0) {
            if (currentDirection == DirectionType.VOID) {
                newdirection = new Direction(currentCellState.getColumnType(), DirectionType.STARTUP);
                this.currentDirection.put(currentCellState.getColumnType(), newdirection.getDirectionType());
                this.previousDirection.put(currentCellState.getColumnType(), currentDirection);
                return newdirection;
            }

            if (currentDirection == DirectionType.UP || currentDirection == DirectionType.STARTUP) {
                newdirection = new Direction(currentCellState.getColumnType(), DirectionType.UP);
            }

            if (currentDirection == DirectionType.DOWN) {
                newdirection = new Direction(currentCellState.getColumnType(), DirectionType.SWITCHUP);
            }

            if (currentDirection == DirectionType.SWITCHUP) {
                newdirection = new Direction(currentCellState.getColumnType(), DirectionType.UP);
            }
        } else {
            if (currentDirection == DirectionType.VOID) {
                newdirection = new Direction(currentCellState.getColumnType(), DirectionType.STARTDOWN);

                this.currentDirection.put(currentCellState.getColumnType(), newdirection.getDirectionType());
                this.previousDirection.put(currentCellState.getColumnType(), currentDirection);
                return newdirection;
            }

            if (currentDirection == DirectionType.DOWN || currentDirection == DirectionType.STARTDOWN) {
                newdirection = new Direction(currentCellState.getColumnType(), DirectionType.DOWN);
            }

            if (currentDirection == DirectionType.UP) {
                newdirection = new Direction(currentCellState.getColumnType(), DirectionType.SWITCHDOWN);
            }

            if (currentDirection == DirectionType.SWITCHDOWN) {
                newdirection = new Direction(currentCellState.getColumnType(), DirectionType.DOWN);
            }
        }

        if (newdirection == null) {
            throw new RuntimeException("BLAH");
        }
        this.currentDirection.put(currentCellState.getColumnType(), newdirection.getDirectionType());
        this.previousDirection.put(currentCellState.getColumnType(), currentDirection);
        return newdirection;
    }

    public Map<ColumnType, DirectionType> getCurrentDirection() {
        return currentDirection;
    }

    public Map<ColumnType, DirectionType> getPreviousDirection() {
        return previousDirection;
    }
}
