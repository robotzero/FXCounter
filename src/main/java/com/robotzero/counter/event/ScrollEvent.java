package com.robotzero.counter.event;

import com.robotzero.counter.domain.ColumnType;

import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public final class ScrollEvent implements MainViewEvent {
    private final Map<String, ChronoField> chronoUnitMap;
    private String parentNodeId;
    private double delta;

    public ScrollEvent(String parentNodeId, double delta) {
        this.parentNodeId = parentNodeId;
        this.delta = delta;
        this.chronoUnitMap = Map.of(
                "seconds", ChronoField.SECOND_OF_MINUTE,
                "minutes", ChronoField.MINUTE_OF_HOUR,
                "hours", ChronoField.HOUR_OF_DAY
        );
    }

    public double getDelta() {
        return delta;
    }

    public ColumnType getColumnType() {
        return ColumnType.valueOf(parentNodeId.toUpperCase());
    }

    public ChronoUnit getChronoUnit() {
        return ChronoUnit.valueOf(parentNodeId.toUpperCase());
    }

    public ChronoField getChronoField() {
        return this.chronoUnitMap.get(parentNodeId.toLowerCase());
    }
}
