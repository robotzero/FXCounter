package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import java.time.LocalTime;

public interface ClockRepository {
    void initialize(LocalTime mainClock);
    void save(ColumnType columnType, LocalTime clock);
    LocalTime get(ColumnType columnType);
}
