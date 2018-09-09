package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ChangeCell;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.DirectionType;
import com.robotzero.counter.event.action.TickAction;
import io.reactivex.Completable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Clock {
    Completable tick(TickAction tickAction, List<ChangeCell> cells);
    Map<ColumnType, ArrayList<Integer>> initialize(DirectionType fromDirection);
}
