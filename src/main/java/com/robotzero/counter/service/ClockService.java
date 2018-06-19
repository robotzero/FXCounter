package com.robotzero.counter.service;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.TimerType;
import com.robotzero.counter.domain.clock.Clock;
import com.robotzero.counter.domain.clock.CurrentClockState;
import io.reactivex.Single;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

public class ClockService {

    private final Clock clock;

    public ClockService(Clock clock) {
        this.clock = clock;
    }

    public Single<CurrentClockState> tick(Direction direction, TimerType timerType, ColumnType columnType) {
        return clock.tick(direction, timerType, columnType);
    }

    public Map<ColumnType, ArrayList<Integer>> initialize(Direction fromDirection) {
        return clock.initialize(fromDirection);
    }
}
