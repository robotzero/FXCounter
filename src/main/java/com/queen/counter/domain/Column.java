package com.queen.counter.domain;

import com.queen.counter.service.OffsetCalculator;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.List;
import java.util.stream.Collectors;

public class Column {

    private List<Cell> columnList;
    private OffsetCalculator offsetCalculator;
    private Clocks clocks;
    private BooleanProperty isRunning = new SimpleBooleanProperty(false);
    private List<TranslateTransition> translateTransitions;

    public Column(List<Cell> columnList, OffsetCalculator offsetCalculator, Clocks clocks) {
        this.columnList = columnList;
        this.offsetCalculator = offsetCalculator;
        this.clocks = clocks;
        translateTransitions = this.columnList.stream().map(Cell::getTranslateTransition).collect(Collectors.toList());
        translateTransitions.forEach(translateTransition -> translateTransition.setOnFinished(event -> {
            this.isRunning().set(false);
        }));
    }

    public void shift(double delta, String name) {
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

    public void setRunning(boolean running) {
//        this.columnList.forEach(cell -> cell.setRunning(running));
//        this.isRunning.set(columnList.stream().filter(Cell::isRunning).findAny().isPresent());
        this.isRunning().set(true);
    }

    public BooleanProperty isRunning() {
        //isRunning.bindBidirectional(new SimpleBooleanProperty(columnList.stream().filter(Cell::isRunning).findAny().isPresent()));
        return isRunning;
    }
}
