package com.queen.counter.domain;

import java.util.List;

public class Column {

    private List<Cell> columnList;

    public Column(List<Cell> columnList) {
        this.columnList = columnList;
    }

    public void shift(double delta) {
        this.columnList.stream().forEach(cell -> {
            cell.setUpTransition(delta);
        });
    }

    public void play() {
        this.columnList.stream().forEach(cell -> {
            cell.animate();
        });
    }
}
