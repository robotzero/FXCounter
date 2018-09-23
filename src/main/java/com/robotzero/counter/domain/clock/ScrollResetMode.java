package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.DirectionType;

import java.time.LocalTime;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ScrollResetMode implements ClockMode {

    private final ClockRepository clockRepository;

    public ScrollResetMode(ClockRepository clockRepository) {
        this.clockRepository = clockRepository;
    }

    @Override
    public void applyNewClockState(BiFunction<ColumnType, Integer, Function<LocalTime, LocalTime>> tick, ColumnType columnType, DirectionType direction) {
        LocalTime currentMain = this.clockRepository.get(ColumnType.MAIN);
        LocalTime afterTickMain = tick.apply(columnType, direction.getDelta() > 0 ? DirectionType.DOWN.getDelta() : DirectionType.UP.getDelta()).apply(currentMain);
        if (columnType.equals(ColumnType.SECONDS)) {
            this.clockRepository.save(ColumnType.SECONDS, tick.apply(columnType, direction.getDelta()).apply(this.clockRepository.get(ColumnType.SECONDS)));
            this.clockRepository.save(ColumnType.MAIN, LocalTime.of(currentMain.getHour(), currentMain.getMinute(), afterTickMain.getSecond()));

        }

        if (columnType.equals(ColumnType.MINUTES)) {
            this.clockRepository.save(ColumnType.MINUTES, tick.apply(columnType, direction.getDelta()).apply(this.clockRepository.get(ColumnType.MINUTES)));
            this.clockRepository.save(ColumnType.MAIN, LocalTime.of(currentMain.getHour(), afterTickMain.getMinute(), currentMain.getSecond()));
        }

        if (columnType.equals(ColumnType.HOURS)) {
            this.clockRepository.save(ColumnType.HOURS, tick.apply(columnType, direction.getDelta()).apply(this.clockRepository.get(ColumnType.HOURS)));
            this.clockRepository.save(ColumnType.MAIN, LocalTime.of(afterTickMain.getHour(), currentMain.getMinute(), currentMain.getSecond()));
        }
    }
}
