package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.clock.ClockRepository;

import java.time.LocalTime;
import java.util.Map;

public class InMemoryClockRepository implements ClockRepository {

    private final Map<ColumnType, LocalTime> clockState;

    public InMemoryClockRepository(Map<ColumnType, LocalTime> clockState) {
        this.clockState = clockState;
    }

    @Override
    public void initialize(LocalTime mainClock) {
        clockState.replace(ColumnType.MAIN, mainClock);
        clockState.replace(ColumnType.SECONDS, clockState.get(ColumnType.SECONDS).withSecond(clockState.get(ColumnType.MAIN).getSecond()));
        clockState.replace(ColumnType.MINUTES, clockState.get(ColumnType.MINUTES).withMinute(clockState.get(ColumnType.MAIN).getMinute()));
        clockState.replace(ColumnType.HOURS, clockState.get(ColumnType.HOURS).withHour(clockState.get(ColumnType.MAIN).getHour()));
    }

    @Override
    public void save(ColumnType columnType, LocalTime clock) {
        clockState.replace(columnType, clock);
    }

    @Override
    public LocalTime get(ColumnType columnType) {
        return clockState.get(columnType);
    }
}
