package com.robotzero.counter.domain;

import com.robotzero.counter.service.DirectionService;
import com.robotzero.counter.service.LocationService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CellStateRepository {

    void initialize(Map<ColumnType, Map<Integer, CellState>> currentCellsState);

    void update(LocationService locationService, DirectionService directionService, ColumnType columnType, double delta);

    Optional<CellState> get(int id);

    List<CellState> getChangeCellStates();

    CellState getChangeCellState(double delta);

    Map<Integer, CellState> getAll(ColumnType columnType);
}
