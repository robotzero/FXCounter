package com.robotzero.counter.domain;

import com.robotzero.counter.service.DirectionService;
import com.robotzero.counter.service.LocationService;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CellStateRepository {

    void initialize(Map<ColumnType, ArrayDeque<CellState>> currentCellsState);

    List<CellState> update(LocationService locationService, DirectionService directionService, ColumnType columnType, double delta);

    Optional<CellState> get(int id);

    ArrayDeque<CellState> getAll(ColumnType columnType);
}
