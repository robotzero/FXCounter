package com.queen.counter.domain;

import com.queen.counter.service.OffsetCalculator;

import java.util.List;

public class Column {

    private List<Cell> columnList;
    private OffsetCalculator offsetCalculator;
    private Clocks clocks;

    public Column(List<Cell> columnList, OffsetCalculator offsetCalculator, Clocks clocks) {
        this.columnList = columnList;
        this.offsetCalculator = offsetCalculator;
        this.clocks = clocks;
    }

    public void shift(double delta, String name) {
        this.offsetCalculator.setLabel(name);
        this.offsetCalculator.setDelta(delta);
        this.columnList.stream().filter(cell -> cell.hasEdgeRectangle(delta))
                                .findAny()
                                .ifPresent(cell -> this.offsetCalculator.setFoundEndgeRectangle(true));

        int timeShift = this.clocks.clockTick(name, delta, this.offsetCalculator.getCurrentOffset());

        this.columnList.stream().filter(cell -> cell.hasChangeTextRectangle(delta))
                                .findAny()
                                .ifPresent(cell -> cell.setLabel(Integer.toString(timeShift)));
        this.columnList.forEach(cell -> cell.setUpTransition(delta));
        this.offsetCalculator.setFoundEndgeRectangle(false);
    }

    public void play() {
        this.columnList.forEach(Cell::animate);
    }
}
