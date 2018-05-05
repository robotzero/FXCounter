package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.TimerType;
import io.reactivex.Observable;

import java.util.List;
import java.util.Map;

public interface Clock {
    Observable<CurrentClockState> tick(Direction direction, TimerType timerType);
    Map<ColumnType, List<Integer>> initialize(Direction fromDirection);
}
