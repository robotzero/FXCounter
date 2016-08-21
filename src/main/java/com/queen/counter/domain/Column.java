package com.queen.counter.domain;

import javafx.animation.Animation;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.SuspendableNo;
import org.reactfx.util.Tuple2;

import java.util.List;

public class Column {

    private List<Cell> columnList;
    private Clocks clocks;
    private BooleanProperty running = new SimpleBooleanProperty(false);
    private BooleanProperty isTicking = new SimpleBooleanProperty(false);
    private BooleanProperty hasTopEdge = new SimpleBooleanProperty(false);
    private BooleanBinding runningBinding;
    private BooleanBinding topEdgeBinding;

    private ColumnType columnType;
    private SuspendableNo indicator = new SuspendableNo();

    public Column(List<Cell> columnList, Clocks clocks, ColumnType columnType) {
        this.columnList = columnList;
        this.clocks = clocks;
        this.columnType = columnType;

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

        this.columnList.forEach(cell -> {
            EventStream changeText = EventStreams.valuesOf(cell.hasChangeTextRectangle());
            EventStream doneReset = indicator.values();
            EventStream<Tuple2<Boolean, Boolean>> canChangeStuff = EventStreams.combine(doneReset, changeText);

            // Allow update cells data only when we are not in the reset mode.
            canChangeStuff.subscribe(combo -> {
                Boolean isResetDone = combo.get1();
                Boolean shouldChangeLabel = combo.get2();
                if (!isResetDone && shouldChangeLabel && cell.getDelta() != 0) {
                    cell.setLabel(Integer.toString(this.clocks.getTimeShift(columnType).get()));
                }

                if (isResetDone) {
                    cell.setLabel(hasTopEdge.get(), clocks.getMainClock(), columnType);
                }
            });
        });
    }

    public void shift(double delta) {
        this.columnList.forEach(cell -> cell.setDelta(delta));
        this.clocks.clockTick(columnType, delta);
    }

    public void play() {
        this.columnList.forEach(Cell::animate);
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
        indicator.suspendWhile(this::resetPositions);
    }

    private void resetPositions() {
        if (!hasTopEdge.get()) {
            this.shift(0);
            this.play();
        }
    }
}
