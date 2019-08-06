package com.robotzero.counter.service;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.CellStateRepository;
import com.robotzero.counter.domain.ColumnType;

import java.util.Deque;
import java.util.Map;

public class CellService {
    private final CellStateRepository cellStateRepository;

    public CellService(CellStateRepository cellStateRepository) {
        this.cellStateRepository = cellStateRepository;
    }

    public void initialize(Map<ColumnType, Deque<CellState>> currentCellsState) {
        this.cellStateRepository.initialize(currentCellsState);
    }

    public CellState get(int id) {
        return this.cellStateRepository.get(id);
    }

    public Deque<CellState> getAll(ColumnType columnType) {
        return this.cellStateRepository.getAll(columnType);
    }
}
