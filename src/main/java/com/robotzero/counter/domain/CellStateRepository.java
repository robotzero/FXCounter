package com.robotzero.counter.domain;

import com.robotzero.counter.service.LocationService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CellStateRepository {

    void initialize(Map<ColumnType, Map<Integer, CellState>> currentCellsState);

    void update(LocationService locationService, ColumnType columnType, double delta);

    void update(Direction direction, ColumnType columnType);

    Optional<CellState> get(int id);

    List<CellState> getChangeCellStates();

    CellState getChangeCellState(double delta);

    Map<Integer, CellState> getAll(ColumnType columnType);
}
