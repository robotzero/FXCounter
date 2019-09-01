package com.robotzero.counter.domain;

import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public enum ColumnType {
    SECONDS(ChronoUnit.SECONDS, ChronoField.SECOND_OF_MINUTE),
    MINUTES(ChronoUnit.MINUTES, ChronoField.MINUTE_OF_HOUR),
    HOURS(ChronoUnit.HOURS, ChronoField.HOUR_OF_DAY),
    MAIN(ChronoUnit.FOREVER, ChronoField.ERA);

    private ChronoUnit chronoUnit;
    private ChronoField chronoField;

    ColumnType(ChronoUnit chronoUnit, ChronoField chronoField) {
        this.chronoUnit = chronoUnit;
        this.chronoField = chronoField;
    }

    public ChronoUnit getChronoUnit() {
        return chronoUnit;
    }

    public ChronoField getChronoField() {
        return chronoField;
    }
}
