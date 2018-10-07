package com.robotzero.counter.domain;

import com.robotzero.counter.service.LocationService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CellStateRepository {

    void initialize(Map<ColumnType, Map<Integer, CellState>> currentCellsState);

    void update(ColumnType columnType, int id, double position, double oldPosition, Direction direction);

    void update(LocationService locationService, Direction direction, ColumnType columnType);

    Optional<CellState> get(int id);

    List<CellState> getChangeCellStates();

    Map<Integer, CellState> getAll(ColumnType columnType);

//    List<CellState> getPreviousChangeCells();
}
