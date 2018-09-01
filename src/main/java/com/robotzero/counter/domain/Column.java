package com.robotzero.counter.domain;

import io.reactivex.Observable;
import javafx.util.Duration;

import java.util.List;

public class Column {

    private List<Cell> columnList;

    public Column(List<Cell> columnList) {
        this.columnList = columnList;
    }

    public Observable<ChangeCell> getChangeCell() {
        return columnList.stream().map(cell -> {
            return cell.getChangeCell();
        }).reduce(Observable.empty(), (current, next) -> {
            return current.mergeWith(next);
        });
//        return columnList.stream()
//                .map(Cell::getChangeCell)
//                .filter(cell -> cell.getColumnType() != ColumnType.VOID)
//                .findFirst().orElseThrow(() -> new RuntimeException("No CHANGE CELL"));
    }

    public void play(DirectionType direction, Duration duration) {
        this.columnList.forEach(cell -> cell.animate(direction, duration));
    }

    public void setLabels(int index, Integer value) {
        this.columnList.get(index).setLabel(value);
    }
}
