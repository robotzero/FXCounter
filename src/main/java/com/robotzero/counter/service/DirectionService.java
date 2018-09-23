package com.robotzero.counter.service;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.DirectionType;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectionService {

    /* We have to track previous directions */
    private Map<ColumnType, DirectionType> previousDirections = new HashMap<>();
    private Map<ColumnType, List<Double>> currentCellsState = new HashMap<>();

    @PostConstruct
    public void initialize() {
        previousDirections.put(ColumnType.SECONDS, null);
        previousDirections.put(ColumnType.MINUTES, null);
        previousDirections.put(ColumnType.HOURS, null);
        previousDirections.put(ColumnType.MAIN, null);
    }

    public Direction calculateDirection(double translateY, double delta, ColumnType columnType, CellState currentCellState) {
        Direction direction = null;
        DirectionType previousDirection = previousDirections.get(columnType);
        if (delta < 0) {
            if (previousDirection == null) {
                direction = new Direction(columnType, DirectionType.STARTUP);
                previousDirections.put(columnType, direction.getDirectionType());
                return direction;
            }

            if (previousDirection.getDelta() == (int) (delta / Math.abs(delta)) || (previousDirection != DirectionType.UP && translateY == -90)) {
                direction = new Direction(columnType, DirectionType.UP);
            } else if (translateY == -90) {
                direction = new Direction(columnType, DirectionType.SWITCHDOWN);
            } else if (translateY == 270) {
                direction = new Direction(columnType, DirectionType.SWITCHUP);
            }
        } else {
            if (previousDirection == null) {
                direction = new Direction(columnType, DirectionType.STARTDOWN);
                previousDirections.put(columnType, direction.getDirectionType());
                return direction;
            }

            if (previousDirection.getDelta() == (int) (delta / Math.abs(delta)) || (previousDirection != DirectionType.DOWN && translateY == 270)) {
                direction = new Direction(columnType, DirectionType.DOWN);
            } else if (translateY == -90) {
                direction = new Direction(columnType, DirectionType.SWITCHDOWN);
            } else if (translateY == 270) {
                direction = new Direction(columnType, DirectionType.SWITCHUP);
            }
        }

        if (direction == null) {
            return new Direction(columnType, DirectionType.VOID);
        }
        previousDirections.put(columnType, direction.getDirectionType());
        return direction;
    }
}
