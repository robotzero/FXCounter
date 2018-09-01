package com.robotzero.counter.service;

import com.robotzero.counter.domain.ChangeCell;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.DirectionType;
import com.robotzero.counter.domain.clock.Clock;
import com.robotzero.counter.domain.clock.CurrentClockState;
import com.robotzero.counter.event.action.TickAction;
import io.reactivex.Single;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClockService {

    private final Clock clock;

    public ClockService(Clock clock) {
        this.clock = clock;
    }

    public Single<CurrentClockState> tick(TickAction action, List<ChangeCell> cells) {
        return clock.tick(action, cells);
    }

    public Map<ColumnType, ArrayList<Integer>> initialize(DirectionType fromDirection) {
        return clock.initialize(fromDirection);
    }
}
