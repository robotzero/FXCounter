package com.robotzero.counter.domain;

import javafx.util.Duration;

import java.util.List;

public class Column {

    private List<Cell> columnList;

    public Column(List<Cell> columnList) {
        this.columnList = columnList;
    }

    public ChangeCell getChangeCell() {
        return columnList.stream()
                .map(Cell::getChangeCell)
                .filter(cell -> cell.getColumnType() != ColumnType.VOID)
                .findFirst().orElseThrow(() -> new RuntimeException("No CHANGE CELL"));
    }

    public void play(Direction direction, Duration duration) {
        this.columnList.forEach(cell -> cell.animate(direction, duration));
    }

    public void setLabels(int index, Integer value) {
        this.columnList.get(index).setLabel(value);
    }
}
