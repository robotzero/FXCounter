package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.*;
import java.util.*;
import java.util.function.Function;

public class InMemoryCellStateRepository implements CellStateRepository {
    private Map<ColumnType, ArrayDeque<CellState>> currentCellsState;

    @Override
    public void initialize(Map<ColumnType, ArrayDeque<CellState>> currentCellsState) {
        this.currentCellsState = currentCellsState;
    }

    @Override
    public void save(ColumnType columnType, ArrayDeque<CellState> newCellState) {
        this.currentCellsState.put(columnType, newCellState);
    }

    @Override
    public CellState get(ColumnType columnType, Function<ArrayDeque<CellState>, CellState> retriever) {
        return retriever.apply(this.currentCellsState.get(columnType));
    }

    @Override
    public CellState getChangeable(ColumnType columnType) {
        CellState top = this.currentCellsState.get(columnType).peekFirst();
        CellState bottom = this.currentCellsState.get(columnType).peekLast();
        if (top.getCurrentLocation().getFromY() == 270 && (top.getPreviousDirection() == DirectionType.VOID || top.getPreviousDirection() == DirectionType.STARTUP || top.getPreviousDirection() == DirectionType.UP || top.getPreviousDirection() == DirectionType.SWITCHUP)) {
            this.currentCellsState.get(columnType).removeFirst();
            this.currentCellsState.get(columnType).addLast(top);
            return top;
        }

        if (bottom.getCurrentLocation().getFromY() == -90 && (bottom.getPreviousDirection() == DirectionType.STARTDOWN || bottom.getPreviousDirection() == DirectionType.DOWN || bottom.getPreviousDirection() == DirectionType.SWITCHDOWN)) {
            this.currentCellsState.get(columnType).removeLast();
            this.currentCellsState.get(columnType).offerFirst(bottom);
            return bottom;
        }

        if (top.getCurrentLocation().getFromY() == -90 && (top.getPreviousDirection() == DirectionType.VOID || top.getPreviousDirection() == DirectionType.UP || top.getPreviousDirection() == DirectionType.STARTUP || top.getPreviousDirection() == DirectionType.SWITCHUP || top.getPreviousDirection() == DirectionType.DOWN)) {
            return top;
        }

        if (bottom.getCurrentLocation().getFromY() == 270 && (bottom.getPreviousDirection() == DirectionType.DOWN || bottom.getPreviousDirection() == DirectionType.STARTDOWN || bottom.getPreviousDirection() == DirectionType.SWITCHDOWN)) {
            return bottom;
        }
        System.out.println(top);
        System.out.println(bottom);
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
