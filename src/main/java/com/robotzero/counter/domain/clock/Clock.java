package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ChangeCell;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.event.action.TickAction;
import io.reactivex.Flowable;
import io.reactivex.Single;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Clock {
    Single<CurrentClockState> tick(Direction direction, TickAction tickAction, List<Flowable<ChangeCell>> cells);
    Map<ColumnType, ArrayList<Integer>> initialize(Direction fromDirection);
}
