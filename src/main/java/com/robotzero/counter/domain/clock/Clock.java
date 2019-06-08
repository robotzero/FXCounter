package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.DirectionType;
import com.robotzero.counter.event.action.TickAction;
import io.reactivex.Completable;
import io.reactivex.Observable;

import java.util.ArrayList;
import java.util.Map;

public interface Clock {
//    Completable tick(TickAction tickAction);
    Observable<CurrentClockState> tick(TickAction tickAction);
    Map<ColumnType, ArrayList<Integer>> initialize(DirectionType fromDirection);
}
