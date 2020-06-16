package com.robotzero.counter.service;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.CellStateRepository;
import com.robotzero.counter.domain.Column;
import com.robotzero.counter.domain.ColumnType;

import java.util.List;
import java.util.Map;

public class CellService {
    private final CellStateRepository cellStateRepository;

    public CellService(final CellStateRepository cellStateRepository) {
        this.cellStateRepository = cellStateRepository;
    }

    public void initialize(final Map<ColumnType, List<CellState>> currentCellsState, final Map<ColumnType, Column> timerColumns) {
        this.cellStateRepository.initialize(currentCellsState, timerColumns);
    }

//    public CellState get(final int id) {
//        return this.cellStateRepository.get(id);
//    }

    public Column getColumn(final ColumnType columnType) {
        return this.cellStateRepository.getColumn(columnType);
    }
}
