package com.robotzero.counter.service;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.CellStateRepository;
import com.robotzero.counter.domain.ColumnType;

import java.util.Map;
import java.util.Optional;

public class CellService {
    private final CellStateRepository cellStateRepository;

    public CellService(CellStateRepository cellStateRepository) {
        this.cellStateRepository = cellStateRepository;
    }

    public void initialize(Map<ColumnType, Map<Integer, CellState>> currentCellsState) {
        this.cellStateRepository.initialize(currentCellsState);
    }

    public Optional<CellState> get(int id) {
        return this.cellStateRepository.get(id);
    }

    public Map<Integer, CellState> getAll(ColumnType columnType) {
        return this.cellStateRepository.getAll(columnType);
    }
}
