package com.robotzero.counter.event.result;

import com.robotzero.counter.domain.ColumnType;

import java.util.List;
import java.util.Map;

public class InitViewResult implements Result {
    private final Map<ColumnType, List<Integer>> initialValues;

    public InitViewResult(Map<ColumnType, List<Integer>> initialValues) {
        this.initialValues = initialValues;
    }

    public Map<ColumnType, List<Integer>> getInitialValues() {
        return initialValues;
    }
}
