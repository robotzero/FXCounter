package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;

import java.time.LocalTime;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ScrollResetMode implements ClockMode {

    private final ClockRepository clockRepository;

    public ScrollResetMode(ClockRepository clockRepository) {
        this.clockRepository = clockRepository;
    }

    @Override
    public void applyNewClockState(BiFunction<ColumnType, Integer, Function<LocalTime, LocalTime>> tick, ColumnType columnType, Direction direction) {
        if (columnType.equals(ColumnType.SECONDS)) {
            this.clockRepository.save(ColumnType.SECONDS, tick.apply(columnType, direction.getDelta()).apply(this.clockRepository.get(ColumnType.SECONDS)));
            this.clockRepository.save(ColumnType.MAIN, this.clockRepository.get(ColumnType.MAIN).withSecond(this.clockRepository.get(ColumnType.SECONDS).getSecond()));
        }

        if (columnType.equals(ColumnType.MINUTES)) {
            this.clockRepository.save(ColumnType.MINUTES, tick.apply(columnType, direction.getDelta()).apply(this.clockRepository.get(ColumnType.MINUTES)));
            this.clockRepository.save(ColumnType.MAIN, this.clockRepository.get(ColumnType.MAIN).withMinute(this.clockRepository.get(ColumnType.MINUTES).getMinute()));
        }

        if (columnType.equals(ColumnType.HOURS)) {
            this.clockRepository.save(ColumnType.HOURS, tick.apply(columnType, direction.getDelta()).apply(this.clockRepository.get(ColumnType.HOURS)));
            this.clockRepository.save(ColumnType.MAIN, this.clockRepository.get(ColumnType.MAIN).withHour(this.clockRepository.get(ColumnType.HOURS).getHour()));
        }

        this.clockRepository.save(ColumnType.MAIN, tick.apply(columnType, direction.getDelta() < 0 ? Direction.DOWN.getDelta() : Direction.UP.getDelta()).apply(this.clockRepository.get(ColumnType.MAIN)));
    }
}