package com.robotzero.counter.service;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.clock.Clock;
import com.robotzero.counter.domain.clock.CurrentClockState;
import com.robotzero.counter.event.action.TickAction;
import io.reactivex.Observable;

import java.util.List;
import java.util.Map;

public class ClockService {

    private final Clock clock;

    public ClockService(Clock clock) {
        this.clock = clock;
    }

    public Observable<CurrentClockState> tick(TickAction action) {
        return clock.tick(action);
    }

    public Map<ColumnType, List<Integer>> initializeLabels() {
        return clock.initializeLabels();
    }

    public void initializeTime() {
        this.clock.initializeTime();
    }
}
