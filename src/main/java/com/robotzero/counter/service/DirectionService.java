package com.robotzero.counter.service;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

public class DirectionService {

    /* We have to track previous directions */
    private Map<ColumnType, Direction> previousDirections = new HashMap<>();

    @PostConstruct
    public void initialize() {
        previousDirections.put(ColumnType.SECONDS, null);
        previousDirections.put(ColumnType.MINUTES, null);
        previousDirections.put(ColumnType.HOURS, null);
    }

    public Direction calculateDirection(double translateY, double delta, ColumnType columnType) {
        Direction direction = null;
        Direction previousDirection = previousDirections.get(columnType);
        if (delta < 0) {
            if (previousDirection == null) {
                direction = Direction.STARTUP;
                previousDirections.put(columnType, direction);
                return direction;
            }

            if (previousDirection.getDelta() == (int) (delta / Math.abs(delta)) || (!previousDirection.equals(Direction.UP) && translateY == -90)) {
                direction = Direction.UP;
            } else if (translateY == -90) {
                direction = Direction.SWITCHDOWN;
            } else if (translateY == 270) {
                direction = Direction.SWITCHUP;
            }
        } else {
            if (previousDirection == null) {
                direction = Direction.STARTDOWN;
                previousDirections.put(columnType, direction);
                return direction;
            }

            if (previousDirection.getDelta() == (int) (delta / Math.abs(delta)) || (!previousDirection.equals(Direction.DOWN) && translateY == 270)) {
                direction = Direction.DOWN;
            } else if (translateY == -90) {
                direction = Direction.SWITCHDOWN;
            } else if (translateY == 270) {
                direction = Direction.SWITCHUP;
            }
        }

        if (direction == null) {
            throw new RuntimeException("Unsupported state");
        }
        previousDirections.put(columnType, direction);
        return direction;
    }
}
