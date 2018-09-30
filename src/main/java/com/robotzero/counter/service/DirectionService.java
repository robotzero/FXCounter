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

    public Direction calculateDirection(CellState currentCellState, double delta) {
        Direction direction = null;
//        DirectionType previousDirection = previousDirections.get(columnType);
        DirectionType previousDirection = currentCellState.getPreviousDirection().getDirectionType();
        if (delta < 0) {
            if (previousDirection == DirectionType.VOID) {
                direction = new Direction(currentCellState.getColumnType(), DirectionType.STARTUP);
                previousDirections.put(currentCellState.getColumnType(), direction.getDirectionType());
                return direction;
            }

            if (previousDirection.getDelta() == (int) (delta / Math.abs(delta)) || (previousDirection != DirectionType.UP && currentCellState.getNewLocation().getFromY() == -90)) {
                direction = new Direction(currentCellState.getColumnType(), DirectionType.UP);
            } else if (currentCellState.getNewLocation().getFromY() == -90) {
                direction = new Direction(currentCellState.getColumnType(), DirectionType.SWITCHDOWN);
            } else if (currentCellState.getNewLocation().getFromY() == 270) {
                direction = new Direction(currentCellState.getColumnType(), DirectionType.SWITCHUP);
            }
        } else {
            if (previousDirection == DirectionType.VOID) {
                direction = new Direction(currentCellState.getColumnType(), DirectionType.STARTDOWN);
                previousDirections.put(currentCellState.getColumnType(), direction.getDirectionType());
                return direction;
            }

            if (previousDirection.getDelta() == (int) (delta / Math.abs(delta)) || (previousDirection != DirectionType.DOWN && currentCellState.getNewLocation().getFromY() == 270)) {
                direction = new Direction(currentCellState.getColumnType(), DirectionType.DOWN);
            } else if (currentCellState.getNewLocation().getFromY() == -90) {
                direction = new Direction(currentCellState.getColumnType(), DirectionType.SWITCHDOWN);
            } else if (currentCellState.getNewLocation().getFromY() == 270) {
                direction = new Direction(currentCellState.getColumnType(), DirectionType.SWITCHUP);
            }
        }

        if (direction == null) {
            return new Direction(currentCellState.getColumnType(), DirectionType.VOID);
        }
        previousDirections.put(currentCellState.getColumnType(), direction.getDirectionType());
        return direction;
    }
}
