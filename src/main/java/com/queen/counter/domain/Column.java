package com.queen.counter.domain;

import com.queen.counter.service.OffsetCalculator;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import java.util.List;

public class Column {

    private List<Cell> columnList;
    private Clocks clocks;
    private BooleanProperty running = new SimpleBooleanProperty(false);
    private BooleanProperty isTicking = new SimpleBooleanProperty(false);
    private BooleanBinding binding;
    private ColumnType columnType;

    public Column(List<Cell> columnList, Clocks clocks, ColumnType columnType) {
        this.columnList = columnList;
        this.clocks = clocks;
        this.columnType = columnType;

        binding = columnList.get(0).isRunning()
                                    .or(columnList.get(1).isRunning())
                                    .or(columnList.get(2).isRunning())
                                    .or(columnList.get(3).isRunning());
        this.running.bind(binding);

        this.columnList.forEach(cell -> {
            EventStream changeText = EventStreams.valuesOf(cell.hasChangeTextRectangle()).filter(Boolean::booleanValue).supply(cell);
            changeText.subscribe(event -> cell.setLabel(Integer.toString(this.clocks.getTimeShift(columnType).get())));
        });
    }

    public void shift(double delta) {
        //this.offsetCalculator.setDelta(delta);
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
}
