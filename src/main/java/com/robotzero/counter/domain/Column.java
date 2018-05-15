package com.robotzero.counter.domain;

import io.reactivex.Observable;

import java.util.List;

public class Column {

    private List<Cell> columnList;
    private ColumnType columnType;

    public Column(List<Cell> columnList, ColumnType columnType) {
        this.columnList = columnList;
        this.columnType = columnType;
    }

    public Observable<Cell> getTopCellObservable(Observable<Direction> directionObservable) {
        return columnList.stream().map(cell -> directionObservable.flatMap(direction -> cell.hasChangeTextRectangle(direction)))
                           .reduce(Observable.empty(), (a, b) -> {
                               return Observable.merge(a, b);
                           });
//        return this.columnList.stream().map(cell -> cell.hasChangeTextRectangle()).reduce(Observable.empty(), (a, b) -> {
//           return Observable.merge(a, b);
//        });
    }

    public void play(Direction direction) {
        this.columnList.forEach(cell -> cell.animate(direction));
    }

    public void setLabels(int index, Integer value) {
        this.columnList.get(index).setLabel(value);
    }
}
