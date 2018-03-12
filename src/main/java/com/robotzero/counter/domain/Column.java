package com.robotzero.counter.domain;

import io.reactivex.Observable;
import io.reactivex.subjects.Subject;
import javafx.animation.Animation;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.reactfx.SuspendableNo;

import java.util.List;

public class Column {

    private final Observable<Cell> topCellObservable;
    private List<Cell> columnList;
    private BooleanProperty running = new SimpleBooleanProperty(false);
    private BooleanProperty hasTopEdge = new SimpleBooleanProperty(false);

    private ColumnType columnType;
    private SuspendableNo resetClicked = new SuspendableNo();
    private Subject<Integer> newLableEvent;

    public Column(List<Cell> columnList, ColumnType columnType, Subject<Integer> newLabelEvent) {
        this.columnList = columnList;
        this.columnType = columnType;
        this.newLableEvent = newLabelEvent;

        columnList.stream().map(Cell::isRunning)
                           .map(runningStatus -> runningStatus.isEqualTo(Animation.Status.RUNNING))
                           .reduce(BooleanExpression::or)
                           .ifPresent(running -> this.running.bind(running));

        columnList.forEach(column -> columnList.stream().map(Cell::isCellOnTop)
                .map(c2 -> c2.or(column.isCellOnTop()))
                .reduce(BooleanExpression::or)
                .ifPresent(hasTop -> this.hasTopEdge.bind(hasTop)));

        // When we are in reset mode / button reset has been clicked set new value of the each cell.
//        resetClicked.noes()
//                    .subscribe(cellList -> this.columnList.forEach(
//                            cell -> {
//                                cell.resetMultiplayer(!hasTopEdge.get());
//                                cell.setLabel(clocks.getMainClock(), columnType);
//                            })
//                    );
        // Grab only top column from the list and transform it into Observable.
        topCellObservable = Observable.fromIterable(this.columnList)
                .flatMap(cell -> cell.hasChangeTextRectangle());
    }

    public Observable<Cell> getTopCellObservable() {
        return topCellObservable;
    }

    public void play(Direction direction) {
        this.columnList.forEach(cell -> cell.animate(direction));
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
