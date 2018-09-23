package com.robotzero.counter.domain;

import java.util.List;
import java.util.Map;

public interface CellStateRepository {

    void initialize(Map<ColumnType, Map<Integer, CellState>> currentCellsState);

    void update(ColumnType columnType, int id, double position, double oldPosition, Direction direction);

    void update(Location location, Direction direction, ColumnType columnType);

    List<CellState> getChangeCellStates();
}
