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

    public Direction calculateDirection(ColumnType columnType, double delta) {
        Direction newdirection = null;
        DirectionType currentDirection = this.currentDirection.get(columnType);
        DirectionType previousDirection = this.previousDirection.get(columnType);
        System.out.println(currentDirection);
        System.out.println(delta);
        if (delta < 0) {
            if (currentDirection == DirectionType.VOID) {
                newdirection = new Direction(columnType, DirectionType.STARTUP);
                this.currentDirection.put(columnType, newdirection.getDirectionType());
                this.previousDirection.put(columnType, currentDirection);
                return newdirection;
            }

            if (currentDirection == DirectionType.UP || currentDirection == DirectionType.STARTUP) {
                newdirection = new Direction(columnType, DirectionType.UP);
            }

            if (currentDirection == DirectionType.DOWN) {
                newdirection = new Direction(columnType, DirectionType.SWITCHUP);
            }

            if (currentDirection == DirectionType.SWITCHUP) {
                newdirection = new Direction(columnType, DirectionType.UP);
            }

            if (currentDirection == DirectionType.STARTDOWN) {
                newdirection = new Direction(columnType, DirectionType.SWITCHUP);
            }

            if (currentDirection == DirectionType.SWITCHDOWN) {
                newdirection = new Direction(columnType, DirectionType.DOWN);
            }
        } else {
            if (currentDirection == DirectionType.VOID) {
                newdirection = new Direction(columnType, DirectionType.STARTDOWN);

                this.currentDirection.put(columnType, newdirection.getDirectionType());
                this.previousDirection.put(columnType, currentDirection);
                return newdirection;
            }

            if (currentDirection == DirectionType.DOWN || currentDirection == DirectionType.STARTDOWN) {
                newdirection = new Direction(columnType, DirectionType.DOWN);
            }

            if (currentDirection == DirectionType.UP) {
                newdirection = new Direction(columnType, DirectionType.SWITCHDOWN);
            }

            if (currentDirection == DirectionType.SWITCHDOWN) {
                newdirection = new Direction(columnType, DirectionType.DOWN);
            }

            if (currentDirection == DirectionType.STARTUP) {
                newdirection = new Direction(columnType, DirectionType.SWITCHDOWN);
            }

            if (currentDirection == DirectionType.SWITCHUP) {
                newdirection = new Direction(columnType, DirectionType.UP);
            }
        }

        if (newdirection == null) {
            throw new RuntimeException("BLAH");
        }
        this.currentDirection.put(columnType, newdirection.getDirectionType());
        this.previousDirection.put(columnType, currentDirection);
        return newdirection;
    }

    public Map<ColumnType, DirectionType> getCurrentDirection() {
        return currentDirection;
    }

    public Map<ColumnType, DirectionType> getPreviousDirection() {
        return previousDirection;
    }
}
