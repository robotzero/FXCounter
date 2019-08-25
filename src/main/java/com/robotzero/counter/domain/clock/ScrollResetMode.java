package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.DirectionType;
import com.robotzero.counter.domain.Tick;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ScrollResetMode implements ClockMode {

    private final ClockRepository clockRepository;

    public ScrollResetMode(ClockRepository clockRepository) {
        this.clockRepository = clockRepository;
    }

    @Override
    public void applyNewClockState(BiFunction<Integer, ChronoUnit, Function<LocalTime, LocalTime>> tick, Tick tickData, DirectionType direction) {
        LocalTime currentMain = this.clockRepository.get(ColumnType.MAIN);
        LocalTime afterTickMain = tick.apply(direction.getDelta() > 0 ? DirectionType.DOWN.getDelta() : DirectionType.UP.getDelta(), tickData.getChronoUnit()).apply(currentMain);
        if (tickData.getColumnType().equals(ColumnType.SECONDS)) {
            this.clockRepository.save(ColumnType.SECONDS, tick.apply(direction.getDelta(), tickData.getChronoUnit()).apply(this.clockRepository.get(ColumnType.SECONDS)));
            this.clockRepository.save(ColumnType.MAIN, LocalTime.of(currentMain.getHour(), currentMain.getMinute(), afterTickMain.getSecond()));

        }

        if (tickData.getColumnType().equals(ColumnType.MINUTES)) {
            this.clockRepository.save(ColumnType.MINUTES, tick.apply(direction.getDelta(), tickData.getChronoUnit()).apply(this.clockRepository.get(ColumnType.MINUTES)));
            this.clockRepository.save(ColumnType.MAIN, LocalTime.of(currentMain.getHour(), afterTickMain.getMinute(), currentMain.getSecond()));
        }

        if (tickData.getColumnType().equals(ColumnType.HOURS)) {
            this.clockRepository.save(ColumnType.HOURS, tick.apply(direction.getDelta(), tickData.getChronoUnit()).apply(this.clockRepository.get(ColumnType.HOURS)));
            this.clockRepository.save(ColumnType.MAIN, LocalTime.of(afterTickMain.getHour(), currentMain.getMinute(), currentMain.getSecond()));
        }
    }
}
