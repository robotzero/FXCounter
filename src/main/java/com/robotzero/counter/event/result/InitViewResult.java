package com.robotzero.counter.event.result;

import com.robotzero.counter.domain.ColumnType;

import java.util.ArrayList;
import java.util.Map;

public class InitViewResult implements Result {
    private final Map<ColumnType, ArrayList<Integer>> initialValues;

    public InitViewResult(Map<ColumnType, ArrayList<Integer>> initialValues) {
        this.initialValues = initialValues;
    }

    public Map<ColumnType, ArrayList<Integer>> getInitialValues() {
        return initialValues;
    }
}
