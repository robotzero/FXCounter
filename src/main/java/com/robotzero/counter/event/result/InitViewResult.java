package com.robotzero.counter.event.result;

import com.google.common.collect.ImmutableSet;
import com.robotzero.counter.domain.ColumnType;
import java.util.Map;

public class InitViewResult implements Result {
  private final Map<ColumnType, ImmutableSet<Integer>> initialValues;

  public InitViewResult(Map<ColumnType, ImmutableSet<Integer>> initialValues) {
    this.initialValues = initialValues;
  }

  public Map<ColumnType, ImmutableSet<Integer>> getInitialValues() {
    return initialValues;
  }
}
