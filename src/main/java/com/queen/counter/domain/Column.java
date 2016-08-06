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
    private OffsetCalculator offsetCalculator;
    private Clocks clocks;
    private BooleanProperty isRunning = new SimpleBooleanProperty(false);
    private BooleanProperty isTicking = new SimpleBooleanProperty(false);

    public Column(List<Cell> columnList, OffsetCalculator offsetCalculator, Clocks clocks) {
        this.columnList = columnList;
        this.offsetCalculator = offsetCalculator;
        this.clocks = clocks;

        BooleanBinding binding = columnList.get(0).isRunning()
                                           .or(columnList.get(1).isRunning())
                                           .or(columnList.get(2).isRunning())
                                           .or(columnList.get(3).isRunning());
        this.isRunning().bind(binding);

        BooleanBinding edgeBinding = columnList.get(0).hasEdgeRectangle()
                                               .or(columnList.get(1).hasEdgeRectangle())
                                               .or(columnList.get(2).hasEdgeRectangle())
                                               .or(columnList.get(3).hasEdgeRectangle());
        this.offsetCalculator.getFoundEdgeRecangle().bind(edgeBinding);

        this.columnList.forEach(cell -> {
            EventStream changeText = EventStreams.changesOf(cell.hasChangeTextRectangle()).supply(cell);
            changeText.filter(c -> ((Cell) c).hasChangeTextRectangle().get()).subscribe(event -> cell.setLabel(Integer.toString(this.clocks.getTimeShift().get())));
        });
    }

    public void shift(double delta, String name) {
        this.offsetCalculator.setDelta(delta);
        this.columnList.forEach(cell -> cell.setDelta(delta));
        this.clocks.clockTick(name, delta, this.offsetCalculator.getCurrentOffset());

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
        return isRunning;
    }

    public BooleanProperty isTicking() {
        return isTicking;
    }
}
