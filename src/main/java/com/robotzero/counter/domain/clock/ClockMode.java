package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.DirectionType;

import java.time.LocalTime;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ClockMode {
    void applyNewClockState(BiFunction<ColumnType, Integer, Function<LocalTime, LocalTime>> tick, ColumnType columnType, DirectionType direction);
}
