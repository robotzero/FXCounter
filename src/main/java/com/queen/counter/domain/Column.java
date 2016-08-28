package com.queen.counter.domain;

import javafx.animation.Animation;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.reactfx.EventSource;
import org.reactfx.EventStreams;
import org.reactfx.SuspendableNo;

import java.util.List;

public class Column {

    private List<Cell> columnList;
    private Clocks clocks;
    private BooleanProperty running = new SimpleBooleanProperty(false);
    private BooleanProperty hasTopEdge = new SimpleBooleanProperty(false);
    private BooleanBinding runningBinding;
    private BooleanBinding topEdgeBinding;

    private ColumnType columnType;
    private SuspendableNo resetClicked = new SuspendableNo();
    private EventSource<Integer> clockEvent;

    public Column(List<Cell> columnList, Clocks clocks, ColumnType columnType, EventSource<Integer> clockEvent) {
        this.columnList = columnList;
        this.clocks = clocks;
        this.columnType = columnType;
        this.clockEvent = clockEvent;

        runningBinding = columnList.get(0).isRunning().isEqualTo(Animation.Status.RUNNING)
                                    .or(columnList.get(1).isRunning().isEqualTo(Animation.Status.RUNNING))
                                    .or(columnList.get(2).isRunning().isEqualTo(Animation.Status.RUNNING))
                                    .or(columnList.get(3).isRunning().isEqualTo(Animation.Status.RUNNING));
        this.running.bind(runningBinding);

        topEdgeBinding = columnList.get(0).hasTopEdgeRectangle()
                                   .or(columnList.get(1).hasTopEdgeRectangle())
                                   .or(columnList.get(2).hasTopEdgeRectangle())
                                   .or(columnList.get(3).hasTopEdgeRectangle());

        this.hasTopEdge.bind(topEdgeBinding);

        // When we are in reset mode / button reset has been clicked set new value of the each cell.
        resetClicked.noes()
                    .subscribe(cellList -> this.columnList.forEach(
                         cell -> cell.setLabel(hasTopEdge.get(), clocks.getMainClock(), columnType))
                    );

        this.columnList.stream().map(cell -> {
            return EventStreams.valuesOf(cell.hasChangeTextRectangle()).suppressWhen(resetClicked).supply(cell);
        }).reduce((current, next) -> {
            return EventStreams.merge(current, next);
        }).ifPresent(changeText -> {
            EventStreams.combine(changeText, clockEvent).subscribe(event -> {
                Cell cell = event.get1();
                Integer timeshift = event.get2();
                if (cell.hasChangeTextRectangle().get() && cell.getDelta() != 0) {
                    cell.setLabel(Integer.toString(timeshift));
                }
            });
        });
    }

    public void play() {
        this.columnList.forEach(Cell::animate);
    }

    public BooleanProperty isRunning() {
        return running;
    }

    public void setLabels() {
        resetClicked.suspendWhile(this::resetPositions);
    }

    private void resetPositions() {
        if (!hasTopEdge.get()) {
            this.play();
        }
    }
}
