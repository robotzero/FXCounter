package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.TimerType;
import io.reactivex.Single;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

public interface Clock {
    Single<CurrentClockState> tick(Direction direction, TimerType timerType, ColumnType columnType);
    Map<ColumnType, ArrayList<Integer>> initialize(Direction fromDirection);
    void calculateResetDifference(LocalTime calculateTo);
}
