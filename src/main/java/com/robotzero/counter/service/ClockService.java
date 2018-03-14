package com.robotzero.counter.service;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.clock.Clock;
import io.reactivex.Observable;

import java.util.List;
import java.util.Map;

public class ClockService {

    private final Clock clock;

    public ClockService(Clock clock) {
        this.clock = clock;
    }

    public Observable<Integer> tick(Direction direction) {
        return clock.tick(direction);
    }

    public Map<ColumnType, Map<Integer, Integer>> initialize(Direction fromDirection) {
        return clock.initialize(fromDirection);
    }
}
