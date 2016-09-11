package com.queen.counter.domain;

import javafx.animation.Animation;
import javafx.beans.binding.BooleanExpression;
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

    private ColumnType columnType;
    private SuspendableNo resetClicked = new SuspendableNo();
    private EventSource<Integer> clockEvent;

    public Column(List<Cell> columnList, Clocks clocks, ColumnType columnType, EventSource<Integer> clockEvent) {
        this.columnList = columnList;
        this.clocks = clocks;
        this.columnType = columnType;
        this.clockEvent = clockEvent;

        columnList.stream().map(Cell::isRunning)
                           .map(runningStatus -> runningStatus.isEqualTo(Animation.Status.RUNNING))
                           .reduce(BooleanExpression::or)
                           .ifPresent(running -> this.running.bind(running));

        // Could be brittle in second map.
        columnList.stream().map(Cell::hasTopEdgeRectangle)
                           .map(hasEdge -> hasEdge.or(hasEdge))
                           .reduce(BooleanExpression::or)
                           .ifPresent(hasTop -> this.hasTopEdge.bind(hasTop));

        // When we are in reset mode / button reset has been clicked set new value of the each cell.
        resetClicked.noes()
                    .subscribe(cellList -> this.columnList.forEach(
                         cell -> cell.setLabel(clocks.getMainClock(), columnType))
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
                    cell.setLabel(timeshift);
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
