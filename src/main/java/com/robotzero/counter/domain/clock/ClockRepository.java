package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import java.time.LocalTime;

public interface ClockRepository {
    void initialize(final LocalTime mainClock);
    void save(final ColumnType columnType, final LocalTime clock);
    LocalTime get(final ColumnType columnType);
}
