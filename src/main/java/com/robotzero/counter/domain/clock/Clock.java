package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.event.action.TickAction;
import io.reactivex.rxjava3.core.Observable;
import java.util.Map;
import java.util.Set;

public interface Clock {
  Observable<CurrentClockState> tick(TickAction tickAction);
  Map<ColumnType, Set<Integer>> initializeLabels();
  void initializeTime();
}
