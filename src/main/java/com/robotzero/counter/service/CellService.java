package com.robotzero.counter.service;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.CellStateRepository;
import com.robotzero.counter.domain.ColumnType;

import java.util.Map;

public class CellService {
    private final CellStateRepository cellStateRepository;

    public CellService(CellStateRepository cellStateRepository) {
        this.cellStateRepository = cellStateRepository;
    }

    public void initialize(Map<ColumnType, Map<Integer, CellState>> currentCellsState) {
        this.cellStateRepository.initialize(currentCellsState);
    }
}
