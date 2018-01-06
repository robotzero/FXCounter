package com.robotzero.counter.domain;

import com.robotzero.counter.domain.clock.Clocks;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import javafx.animation.Animation;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.reactfx.SuspendableNo;

import java.util.List;

public class Column {

    private List<Cell> columnList;
    private Clocks clocks;
    private BooleanProperty running = new SimpleBooleanProperty(false);
    private BooleanProperty hasTopEdge = new SimpleBooleanProperty(false);

    private ColumnType columnType;
    private SuspendableNo resetClicked = new SuspendableNo();
    private Subject<Integer> clockEvent;

    public Column(List<Cell> columnList, Clocks clocks, ColumnType columnType, Subject<Integer> clockEvent) {
        this.columnList = columnList;
        this.clocks = clocks;
        this.columnType = columnType;
        this.clockEvent = clockEvent;

        columnList.stream().map(Cell::isRunning)
                           .map(runningStatus -> runningStatus.isEqualTo(Animation.Status.RUNNING))
                           .reduce(BooleanExpression::or)
                           .ifPresent(running -> this.running.bind(running));

        columnList.forEach(column -> columnList.stream().map(Cell::isCellOnTop)
                .map(c2 -> c2.or(column.isCellOnTop()))
                .reduce(BooleanExpression::or)
                .ifPresent(hasTop -> this.hasTopEdge.bind(hasTop)));

//         Could be brittle in second map.
//        columnList.stream().map(Cell::hasTopEdgeRectangle)
//                           .map(hasEdge -> hasEdge.or(hasEdge))
//                           .reduce(BooleanExpression::or)
//                           .ifPresent(hasTop -> this.hasTopEdge.bind(hasTop));

        // When we are in reset mode / button reset has been clicked set new value of the each cell.
        resetClicked.noes()
                    .subscribe(cellList -> this.columnList.forEach(
                            cell -> {
                                cell.resetMultiplayer(!hasTopEdge.get());
                                cell.setLabel(clocks.getMainClock(), columnType);
                            })
                    );

        clockEvent.skipWhile(a -> {
            return this.columnList.stream().noneMatch(cell -> cell.hasChangeTextRectangle().get());
        }).subscribe(event -> {
            this.columnList.stream().filter(cell -> cell.hasChangeTextRectangle().get()).findFirst().ifPresent(cell -> cell.setLabel(event.intValue()));
        });
    }

    public void play() {
        this.columnList.forEach(Cell::animate);
    }

    public void setLabels() {
        resetClicked.suspendWhile(this::resetPositions);
    }

    private void resetPositions() {
        if (!hasTopEdge.get()) {
            this.columnList.forEach(Cell::animateReset);
        }
    }
}
