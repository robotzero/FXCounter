package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.service.DirectionService;
import com.robotzero.counter.service.LocationService;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryCellStateRepository implements CellStateRepository {
    private Map<ColumnType, Map<Integer, CellState>> currentCellsState;
    private ArrayDeque<CellState> blah;
    private List<CellState> blah2;
    private ArrayDeque<CellState> seconds;
    private ArrayDeque<CellState> previous;

    @Override
    public void initialize(Map<ColumnType, Map<Integer, CellState>> currentCellsState) {
        this.seconds = currentCellsState.get(ColumnType.SECONDS).values().stream().sorted((i1, i2) -> {
            Integer test = (int) i1.getCurrentLocation().getFromY();
            Integer test2 = (int) i2.getCurrentLocation().getFromY();
            return test.compareTo(test2);
        }).collect(Collectors.toCollection(ArrayDeque::new));

        this.previous = this.seconds.clone();
        this.currentCellsState = currentCellsState;
    }

    private void update(ColumnType columnType, int id, double position, double oldPosition, DirectionType current, DirectionType previous) {
        CellState cellState = currentCellsState.get(columnType).get(id);
        CellState newCellState = cellState.createNew(position, oldPosition, current, previous);
        Map<Integer, CellState> currentList = currentCellsState.get(columnType);
        currentList.put(id, newCellState);
//        this.blah2 = this.blah.stream().map(cellState1 -> {
//            if (cellState1.getId() == id) {
//                return newCellState;
//            };
//            return cellState1;
//        }).collect(Collectors.toList());
    }

    @Override
    public void update(LocationService locationService, DirectionService directionService, ColumnType columnType, double delta) {
//        this.blah = List.copyOf(this.seconds);
        currentCellsState.entrySet().stream().filter(entry -> entry.getKey() == columnType)
                .flatMap(entry -> {
                    return entry.getValue().entrySet().stream();
                }).map(cellState -> {
                    return cellState.getValue();
                })
                .forEach(entry -> {
                    double fromY = locationService.calculateFromY(new SimpleIntegerProperty(90), delta, entry.getCurrentLocation().getToY());
                    double toY = locationService.calculateToY(new SimpleIntegerProperty(90), delta, entry.getCurrentLocation().getToY());
//                    System.out.println("Cell with " + entry.getId() + " is updated to " + "FROM Y " + fromY + " TO Y " + toY);
                    this.update(columnType, entry.getId(),  fromY, toY, directionService.getCurrentDirection().get(columnType), directionService.getPreviousDirection().get(columnType));
                });

        this.blah = this.seconds.clone();
        this.seconds = this.blah.stream().map(cellState -> {
            double fromY = locationService.calculateFromY(new SimpleIntegerProperty(90), delta, cellState.getCurrentLocation().getToY());
            double toY = locationService.calculateToY(new SimpleIntegerProperty(90), delta, cellState.getCurrentLocation().getToY());
            return cellState.createNew(fromY, toY, directionService.getCurrentDirection().get(ColumnType.SECONDS), directionService.getPreviousDirection().get(ColumnType.SECONDS));
        }).collect(Collectors.toCollection(ArrayDeque::new));
//        this.seconds = new ArrayDeque<>(this.blah2);

        CellState top = this.seconds.peekFirst();
        CellState bottom = this.seconds.peekLast();
        System.out.println(top);
        System.out.println(bottom);

        if (top.getCurrentLocation().getFromY() == -90 && (top.getCurrentDirection() == DirectionType.VOID || top.getCurrentDirection() == DirectionType.UP || top.getCurrentDirection() == DirectionType.DOWN || top.getCurrentDirection() == DirectionType.STARTDOWN || top.getCurrentDirection() == DirectionType.SWITCHDOWN)) {
            this.seconds.removeFirst();
            this.seconds.addLast(top);
            System.out.println("TOP " + top.getId());
        }

        if (bottom.getCurrentLocation().getFromY() == 270 && (bottom.getCurrentDirection() == DirectionType.VOID || bottom.getCurrentDirection() == DirectionType.UP || bottom.getCurrentDirection() == DirectionType.DOWN || bottom.getCurrentDirection() == DirectionType.STARTUP || bottom.getCurrentDirection() == DirectionType.SWITCHUP)) {
            this.seconds.removeLast();
            this.seconds.offerFirst(bottom);
            System.out.println("BOTTOM " + bottom.getId());
        }
    }

    @Override
    public Optional<CellState> get(int id) {
        return currentCellsState.values().stream().map(entry -> entry.get(id)).findFirst();
    }

    @Override
    public List<CellState> getChangeCellStates() {
        return currentCellsState.entrySet().stream().flatMap(entry -> {
            return entry.getValue().values().stream().filter(CellState::isChangeable);
        }).collect(Collectors.toList());
    }

    @Override
    public CellState getChangeCellState(double delta) {
        if (delta < 0) {
            return this.seconds.getLast();
        }
        return this.seconds.getLast();
    }

    @Override
    public Map<Integer, CellState> getAll(ColumnType columnType) {
        return this.currentCellsState.get(columnType);
    }

    @Override
    public String toString() {
        return "InMemoryCellStateRepository{" +
                "currentCellsState=" + currentCellsState +
                '}';
    }
}
