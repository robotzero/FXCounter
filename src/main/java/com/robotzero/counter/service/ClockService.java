package com.robotzero.counter.service;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.clock.Clock;
import com.robotzero.counter.domain.clock.CurrentClockState;
import com.robotzero.counter.domain.clock.LocalTimeClock.InitCellsMetadata;
import com.robotzero.counter.event.action.TickAction;
import io.reactivex.rxjava3.core.Observable;
import java.util.Map;
import java.util.Set;

public class ClockService {
  private final Clock clock;

  public ClockService(Clock clock) {
    this.clock = clock;
  }

  public Observable<CurrentClockState> tick(TickAction action) {
    return clock.tick(action);
  }

  public Map<ColumnType, Set<InitCellsMetadata>> initializeLabels() {
    return clock.initializeLabels();
  }

  public void initializeTime() {
    this.clock.initializeTime();
  }
}
