package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.service.DirectionService;
import com.robotzero.counter.service.LocationService;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryCellStateRepository implements CellStateRepository {
    private Map<ColumnType, ArrayDeque<CellState>> currentCellsState;

    @Override
    public void initialize(Map<ColumnType, ArrayDeque<CellState>> currentCellsState) {
        this.currentCellsState = currentCellsState;
    }

    @Override
    public CellState update(LocationService locationService, DirectionService directionService, ColumnType columnType, double delta) {
        ArrayDeque<CellState> updatedCellState = this.currentCellsState.get(columnType).stream().map(cellState -> {
            double fromY = locationService.calculateFromY(new SimpleIntegerProperty(90), delta, cellState.getCurrentLocation().getToY());
            double toY = locationService.calculateToY(new SimpleIntegerProperty(90), delta, cellState.getCurrentLocation().getToY());
            return cellState.createNew(fromY, toY, directionService.getCurrentDirection().get(columnType), directionService.getPreviousDirection().get(columnType));
        }).collect(Collectors.toCollection(ArrayDeque::new));
        this.currentCellsState.put(columnType, updatedCellState);

        CellState top = this.currentCellsState.get(columnType).peekFirst();
        CellState bottom = this.currentCellsState.get(columnType).peekLast();
        if (top.getCurrentLocation().getFromY() == 270 && (top.getCurrentDirection() == DirectionType.VOID || top.getCurrentDirection() == DirectionType.STARTUP || top.getCurrentDirection() == DirectionType.UP || top.getCurrentDirection() == DirectionType.SWITCHUP)) {
            this.currentCellsState.get(columnType).removeFirst();
            this.currentCellsState.get(columnType).addLast(top);
            return top;
        }

        if (bottom.getCurrentLocation().getFromY() == -90 && (bottom.getCurrentDirection() == DirectionType.STARTDOWN || bottom.getCurrentDirection() == DirectionType.DOWN || bottom.getCurrentDirection() == DirectionType.SWITCHDOWN)) {
            this.currentCellsState.get(columnType).removeLast();
            this.currentCellsState.get(columnType).offerFirst(bottom);
            return bottom;
        }

        if (top.getCurrentLocation().getFromY() == -90 && (top.getCurrentDirection() == DirectionType.VOID || top.getCurrentDirection() == DirectionType.UP)) {
            return top;
        }

        if (bottom.getCurrentLocation().getFromY() == 270 && bottom.getCurrentDirection() == DirectionType.DOWN) {
            return bottom;
        }
        throw new RuntimeException("NAH");
    }

    @Override
    public Optional<CellState> get(int id) {
        return currentCellsState.values().stream().flatMap(Collection::stream).filter(cellState -> cellState.getId() == id).findFirst();
    }

    @Override
    public ArrayDeque<CellState> getAll(ColumnType columnType) {
        return this.currentCellsState.get(columnType);
    }

    @Override
    public String toString() {
        return "InMemoryCellStateRepository{" +
                "currentCellsState=" + currentCellsState +
                '}';
    }
}
