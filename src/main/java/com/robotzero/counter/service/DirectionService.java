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
        DirectionType previousDirection = currentCellState.getCurrentDirection().getDirectionType();
        System.out.println(delta);
//        direction = new Direction(currentCellState.getColumnType(), DirectionType.STARTUP);
//        System.out.println("=================");
//        System.out.println("Previous direction " + previousDirection + " " + currentCellState.toString());
        if (delta < 0) {
            if (previousDirection == DirectionType.VOID) {
                direction = new Direction(currentCellState.getColumnType(), DirectionType.STARTUP);
                previousDirections.put(currentCellState.getColumnType(), direction.getDirectionType());
                System.out.println("New direction " + direction.getDirectionType() + " " + currentCellState.toString());
                System.out.println("=================");
                return direction;
            }

            if (previousDirection.getDelta() == (int) (delta / Math.abs(delta)) || (previousDirection != DirectionType.UP && currentCellState.getPreviousLocation().getFromY() == -90)) {
                System.out.println("BLAH3");
                direction = new Direction(currentCellState.getColumnType(), DirectionType.UP);
            } else if (currentCellState.getNewLocation().getFromY() == -90) {
                System.out.println("BLAH4");
                direction = new Direction(currentCellState.getColumnType(), DirectionType.SWITCHDOWN);
            } else if (currentCellState.getNewLocation().getFromY() == 270) {
                System.out.println("BLAH5");
                direction = new Direction(currentCellState.getColumnType(), DirectionType.SWITCHUP);
            }
        } else {
            if (previousDirection == DirectionType.VOID) {
                direction = new Direction(currentCellState.getColumnType(), DirectionType.STARTDOWN);
                previousDirections.put(currentCellState.getColumnType(), direction.getDirectionType());
                System.out.println("New direction " + direction.getDirectionType() + " " + currentCellState.toString());
                System.out.println("=================");
                return direction;
            }

            if (previousDirection.getDelta() == (int) (delta / Math.abs(delta)) || (previousDirection != DirectionType.DOWN && (currentCellState.getPreviousLocation().getFromY() == 270))) {
                System.out.println("BLAH");
                direction = new Direction(currentCellState.getColumnType(), DirectionType.DOWN);
            } else if (currentCellState.getNewLocation().getFromY() == -90) {
                System.out.println("BLAH1");
                direction = new Direction(currentCellState.getColumnType(), DirectionType.SWITCHDOWN);
            } else if (currentCellState.getNewLocation().getFromY() == 270) {
                System.out.println("BLAH2");
                direction = new Direction(currentCellState.getColumnType(), DirectionType.SWITCHUP);
            }
        }

        System.out.println("New direction " + direction.getDirectionType() + " " + currentCellState.toString());
        System.out.println("=================");
        if (direction == null) {
            return new Direction(currentCellState.getColumnType(), DirectionType.VOID);
        }
        previousDirections.put(currentCellState.getColumnType(), direction.getDirectionType());
        return direction;
    }
}
