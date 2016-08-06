package com.queen.counter.domain;

import com.queen.counter.service.OffsetCalculator;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.List;

public class Column {

    private List<Cell> columnList;
    private OffsetCalculator offsetCalculator;
    private Clocks clocks;
    private BooleanProperty isRunning = new SimpleBooleanProperty(false);
    private BooleanProperty isTicking = new SimpleBooleanProperty(false);

    public Column(List<Cell> columnList, OffsetCalculator offsetCalculator, Clocks clocks) {
        this.columnList = columnList;
        this.offsetCalculator = offsetCalculator;
        this.clocks = clocks;

//        this.columnList.stream().map(Cell::isRunning).reduce((current, next) -> {
//            BooleanBinding booleanBinding = current.or(next);
//            this.isRunning().bind(booleanBinding);
//            return isRunning;
//        });

        BooleanBinding binding = columnList.get(0).isRunning().or(columnList.get(1).isRunning()).or(columnList.get(2).isRunning()).or(columnList.get(3).isRunning());
        isRunning().bind(binding);
    }

    public void shift(double delta, String name) {
        this.offsetCalculator.setDelta(delta);
        this.columnList.forEach(cell -> cell.setDelta(delta));
        this.columnList.stream().filter(Cell::hasEdgeRectangle)
                                .findAny()
                                .ifPresent(cell -> this.offsetCalculator.setFoundEndgeRectangle(true));

        int timeShift = this.clocks.clockTick(name, delta, this.offsetCalculator.getCurrentOffset());

        this.columnList.stream().filter(Cell::hasChangeTextRectangle)
                                .findAny()
                                .ifPresent(cell -> cell.setLabel(Integer.toString(timeShift)));
        this.offsetCalculator.setFoundEndgeRectangle(false);
    }

    public void play() {
        this.columnList.forEach(Cell::animate);
    }

    public void setRunning(boolean running) {
        this.columnList.forEach(cell -> cell.setRunning(running));
    }

    public void setTicking(boolean ticking) {
        this.isTicking.set(ticking);
    }

    public BooleanProperty isRunning() {
        return isRunning;
    }

    public BooleanProperty isTicking() {
        return isTicking;
    }
}
