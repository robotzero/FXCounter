package com.robotzero.counter.event.result;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.clock.LocalTimeClock.InitCellsMetadata;
import java.util.Map;
import java.util.Set;

public class InitViewResult implements Result {
  private final Map<ColumnType, Set<InitCellsMetadata>> initialValues;

  public InitViewResult(Map<ColumnType, Set<InitCellsMetadata>> initialValues) {
    this.initialValues = initialValues;
  }

  public Map<ColumnType, Set<InitCellsMetadata>> getInitialValues() {
    return initialValues;
  }
}
