package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.event.action.TickAction;
import io.reactivex.Observable;

import java.util.List;
import java.util.Map;

public interface Clock {
    Observable<CurrentClockState> tick(TickAction tickAction);
    Map<ColumnType, List<Integer>> initializeLabels();
    void initializeTime();
}
