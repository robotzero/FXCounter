package com.robotzero.counter.domain;

import javafx.util.Duration;

import java.util.List;

public class Column {

    private List<Cell> columnList;

    public Column(List<Cell> columnList) {
        this.columnList = columnList;
//        this.columnList.forEach(c -> {
//            if (c.getColumnType() == ColumnType.SECONDS) {
//                System.out.println(c);
//            }
//        });
    }

    public void play(DirectionType direction, Duration duration) {
        this.columnList.forEach(cell -> cell.animate(direction, duration));
    }

    public void play(CellState cellState, Duration duration) {
        this.columnList.forEach(cell -> cell.animate(cellState, duration));
    }

    public void setLabels(int index, Integer value) {
        this.columnList.get(index).setLabel(value);
    }

    public void setLabel(int id, Integer value) {
//        this.columnList.forEach(c -> {
//            if (c.getColumnType() == ColumnType.SECONDS) {
//                System.out.println(c);
//            }
//        });
        this.columnList.stream().filter(cell -> {
            return cell.getId() == id;
        }).findFirst().ifPresent(cell -> {
            cell.setLabel(value);
        });
    }
}
