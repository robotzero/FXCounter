package com.queen.counter.domain;

import com.queen.counter.service.OffsetCalculator;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.TaskStream;

import java.util.List;

public class Column {

    private List<Cell> columnList;
    private OffsetCalculator offsetCalculator;
    private Clocks clocks;
    private BooleanProperty running = new SimpleBooleanProperty(false);
    private BooleanProperty isTicking = new SimpleBooleanProperty(false);
    private BooleanBinding binding, edgeBinding;
    private ColumnType columnType;

    public Column(List<Cell> columnList, OffsetCalculator offsetCalculator, Clocks clocks, ColumnType columnType) {
        this.columnList = columnList;
        this.offsetCalculator = offsetCalculator;
        this.clocks = clocks;
        this.columnType = columnType;

         binding = columnList.get(0).isRunning()
                                    .or(columnList.get(1).isRunning())
                                    .or(columnList.get(2).isRunning())
                                    .or(columnList.get(3).isRunning());
        this.running.bind(binding);

        edgeBinding = columnList.get(0).hasEdgeRectangle()
                                       .or(columnList.get(1).hasEdgeRectangle())
                                       .or(columnList.get(2).hasEdgeRectangle())
                                       .or(columnList.get(3).hasEdgeRectangle());

        this.offsetCalculator.getFoundEdgeRecangle().bind(edgeBinding);

        this.columnList.forEach(cell -> {
            EventStream changeText = EventStreams.valuesOf(cell.hasChangeTextRectangle()).supply(cell).filter(c -> c.hasChangeTextRectangle().get());
            changeText.subscribe(event -> cell.setLabel(Integer.toString(this.clocks.getTimeShift(columnType).get())));
        });
    }

    public void shift(double delta, String name) {
        this.offsetCalculator.setDelta(delta);
        this.columnList.forEach(cell -> cell.setDelta(delta));
        System.out.println(this.offsetCalculator.getCurrentOffset());
        this.clocks.clockTick(columnType, delta, this.offsetCalculator.getCurrentOffset());

//        this.columnList.stream().filter(cell -> cell.hasChangeTextRectangle().get())
//                                .findAny()
//                                .ifPresent(cell -> cell.setLabel(Integer.toString(this.clocks.getTimeShift().get())));
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
