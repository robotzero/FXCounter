package com.queen.counter.domain;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import java.time.LocalTime;
import java.util.List;

public class Column {

    private List<Cell> columnList;
    private Clocks clocks;
    private BooleanProperty running = new SimpleBooleanProperty(false);
    private BooleanProperty isTicking = new SimpleBooleanProperty(false);
    private BooleanProperty hasTopEdge = new SimpleBooleanProperty(false);
    private BooleanBinding runningBinding;
    private BooleanBinding topEdgeBinding;
    private BooleanBinding resettingBinding;

    private ColumnType columnType;
    private BooleanProperty isResetting = new SimpleBooleanProperty(false);

    public Column(List<Cell> columnList, Clocks clocks, ColumnType columnType) {
        this.columnList = columnList;
        this.clocks = clocks;
        this.columnType = columnType;

        runningBinding = columnList.get(0).isRunning()
                                    .or(columnList.get(1).isRunning())
                                    .or(columnList.get(2).isRunning())
                                    .or(columnList.get(3).isRunning());
        this.running.bind(runningBinding);

        topEdgeBinding = columnList.get(0).hasTopEdgeRectangle()
                                   .or(columnList.get(1).hasTopEdgeRectangle())
                                   .or(columnList.get(2).hasTopEdgeRectangle())
                                   .or(columnList.get(3).hasTopEdgeRectangle());

        this.hasTopEdge.bind(topEdgeBinding);

        resettingBinding = columnList.get(0).isDuringReset()
                .or(columnList.get(1).isDuringReset())
                .or(columnList.get(2).isDuringReset())
                .or(columnList.get(3).isDuringReset());

        this.columnList.forEach(cell -> {
            EventStream changeText = EventStreams.valuesOf(cell.hasChangeTextRectangle()).filter(Boolean::booleanValue).supply(cell).suppressWhen(resettingBinding);
            changeText.subscribe(event -> cell.setLabel(Integer.toString(this.clocks.getTimeShift(columnType).get())));
        });
    }

    public void shift(double delta) {
        this.columnList.forEach(cell -> cell.setDelta(delta));
        this.clocks.clockTick(columnType, delta);
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
        return running;
    }

    public BooleanProperty isTicking() {
        return isTicking;
    }

    public void setLabels() {
        this.setResetting();
        this.resetPositions();
        this.clocks.initializeClocks(LocalTime.of(0, 0, 0));

//        for (int i = 0; i < columnList.size(); i++) {
//            int value = 0;
//            if (columnType.equals(ColumnType.SECONDS)) {
//                value = this.clocks.getMainClock().getSecond() - i + 2;
//            }
//            if (columnType.equals(ColumnType.MINUTES)) {
//                value = this.clocks.getMainClock().getMinute() - i + 2;
//            }
//
//            value = value == -1 ? 59 : value;
//            columnList.get(i).setLabel(Integer.toString(value));
//        }
    }

    private void resetPositions() {
        if (!hasTopEdge.get()) {
            this.shift(-60);
            this.play();
        }
    }

    private void setResetting() {
        this.columnList.forEach(cell -> cell.setResetting(true));
    }
}
