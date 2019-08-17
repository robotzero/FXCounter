package com.robotzero.counter.infrastructure.memory;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.clock.ClockRepository;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class InMemoryClockRepository implements ClockRepository {

    private final Map<ColumnType, LocalTime> clockState;

    {
        clockState = new HashMap<>();
    }

    @Override
    public void initialize(LocalTime mainClock) {
        clockState.put(ColumnType.MAIN, mainClock);
        clockState.put(ColumnType.SECONDS, LocalTime.of(0, 0, clockState.get(ColumnType.MAIN).getSecond()));
        clockState.put(ColumnType.MINUTES, LocalTime.of(0, clockState.get(ColumnType.MAIN).getMinute(), 0));
        clockState.put(ColumnType.HOURS, LocalTime.of(clockState.get(ColumnType.MAIN).getHour(), 0, 0));
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
