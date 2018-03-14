package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import io.reactivex.Observable;

import java.util.Map;

public interface Clock {
    Observable<Integer> tick(Direction direction);
    Map<ColumnType, Map<Integer, Integer>> initialize(Direction fromDirection);
}
