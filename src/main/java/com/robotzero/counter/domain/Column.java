package com.robotzero.counter.domain;

import javafx.util.Duration;

import java.util.List;

public class Column {

    private final List<Cell> cells;

    public Column(List<Cell> cells) {
        this.cells = cells;
    }

    public void play(Duration duration) {
        this.cells.parallelStream().forEach(cell -> cell.animate(duration));
    }

    public void setLabels(int index, Integer value) {
        this.cells.get(index).setLabel(value);
    }

    public void setLabel(int id, Integer value) {
        this.cells.stream().filter(cell -> {
            return cell.getId() == id;
        }).findFirst().ifPresent(cell -> {
            cell.setLabel(value);
        });
    }

    public void setNewCellState() {

    }

    public List<Cell> getCells() {
        return this.cells;
    }
}
