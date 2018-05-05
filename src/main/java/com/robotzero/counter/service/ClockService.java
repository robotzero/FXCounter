package com.robotzero.counter.service;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.TimerType;
import com.robotzero.counter.domain.clock.Clock;
import com.robotzero.counter.domain.clock.CurrentClockState;
import io.reactivex.Observable;

import java.util.List;
import java.util.Map;

public class ClockService {

    private final Clock clock;

    public ClockService(Clock clock) {
        this.clock = clock;
    }

    public Observable<CurrentClockState> tick(Direction direction, TimerType timerType) {
        return clock.tick(direction, timerType);
    }

    public Map<ColumnType, List<Integer>> initialize(Direction fromDirection) {
        return clock.initialize(fromDirection);
    }
}
