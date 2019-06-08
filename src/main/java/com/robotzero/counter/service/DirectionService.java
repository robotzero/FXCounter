package com.robotzero.counter.service;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.DirectionType;

public class DirectionService {

    public Direction calculateDirection(ColumnType columnType, DirectionType currentDirection, double delta) {
        Direction newdirection = null;
        System.out.println(currentDirection);
        System.out.println(delta);
        if (delta < 0) {
            if (currentDirection == DirectionType.VOID) {
                newdirection = new Direction(columnType, DirectionType.STARTUP);
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
        return newdirection;
    }
}
