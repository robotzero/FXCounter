package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;

import java.time.LocalTime;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TickMode implements ClockMode {

    private ClockRepository clockRepository;

    public TickMode(ClockRepository clockRepository) {
        this.clockRepository = clockRepository;
    }

    public void applyNewClockState(BiFunction<ColumnType, Integer, Function<LocalTime, LocalTime>> tick, ColumnType columnType, Direction direction) {
        if (columnType == ColumnType.SECONDS) {
            clockRepository.save(ColumnType.MAIN, tick.apply(ColumnType.SECONDS, direction.getDelta()).apply(clockRepository.get(ColumnType.MAIN)));
            clockRepository.save(ColumnType.SECONDS, tick.apply(ColumnType.SECONDS, direction.getDelta()).apply(clockRepository.get(ColumnType.SECONDS)));
        }
        if (columnType == ColumnType.MINUTES) {
            clockRepository.save(ColumnType.MINUTES, tick.apply(ColumnType.MINUTES, Direction.UP.getDelta()).apply(clockRepository.get(ColumnType.MINUTES)));
        }
        if (columnType == ColumnType.HOURS) {
            clockRepository.save(ColumnType.HOURS, tick.apply(ColumnType.HOURS, Direction.UP.getDelta()).apply(clockRepository.get(ColumnType.HOURS)));
        }
    }
}
