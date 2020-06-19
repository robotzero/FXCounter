package com.robotzero.counter.event.result;

import com.robotzero.counter.domain.ColumnType;
import java.util.Map;
import java.util.Set;

public class InitViewResult implements Result {
  private final Map<ColumnType, Set<Integer>> initialValues;

  public InitViewResult(Map<ColumnType, Set<Integer>> initialValues) {
    this.initialValues = initialValues;
  }

  public Map<ColumnType, Set<Integer>> getInitialValues() {
    return initialValues;
  }
}
