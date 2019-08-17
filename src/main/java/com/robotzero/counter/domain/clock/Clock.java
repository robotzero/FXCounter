package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.DirectionType;
import com.robotzero.counter.event.action.TickAction;
import io.reactivex.Observable;

import java.util.ArrayList;
import java.util.Map;

public interface Clock {
    Observable<CurrentClockState> tick(TickAction tickAction);
    Map<ColumnType, ArrayList<Integer>> initializeLabels(DirectionType fromDirection);
    void initializeTime();
}
