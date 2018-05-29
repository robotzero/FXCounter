package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.TimerType;
import io.reactivex.Observable;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class LocalTimeClock implements Clock {

    private final TimerRepository timerRepository;
    private LocalTime mainClock          = LocalTime.of(0, 0, 0);
    private LocalTime scrollSecondsClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollMinutesClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollHoursClock   = LocalTime.of(0, 0, 0);

    private Predicate<Integer> shouldTick = clockState -> clockState == 0;

    private BiFunction<ColumnType, Direction, BiFunction<LocalTime, TimerType, LocalTime>> tick = (columnType, direction) -> {
        if (columnType.equals(ColumnType.SECONDS)) {
            return (localTime, timerType) -> localTime.plusSeconds(direction.getDelta());
        }

        if (columnType.equals(ColumnType.MINUTES)) {
            return (localTime, timerType) -> localTime.plusMinutes(direction.getDelta());
        }

        if (columnType.equals(ColumnType.HOURS)) {
            return (localTime, timerType) -> localTime.plusHours(direction.getDelta());
        }

        return (localTime, timerType) -> localTime;
    };

    private final long MIN = ChronoUnit.MINUTES.getDuration().toMinutes();
    private final long HR  = ChronoUnit.HOURS.getDuration().toHours();

    public LocalTimeClock(TimerRepository timerRepository) {
        this.timerRepository = timerRepository;
    }

    @PostConstruct
    public void initialize() {
        this.mainClock = Optional.ofNullable(timerRepository.selectLatest()).orElseGet(() -> {
            com.robotzero.counter.entity.Clock savedTimer = new com.robotzero.counter.entity.Clock();
            savedTimer.setSavedTimer(LocalTime.of(22, 1, 10));
            return savedTimer;
        }).getSavedTimer();
        this.scrollSecondsClock = this.scrollSecondsClock.withSecond(mainClock.getSecond());
        this.scrollMinutesClock = this.scrollMinutesClock.withMinute(mainClock.getMinute());
        this.scrollHoursClock = this.scrollHoursClock.withHour(mainClock.getHour());
    }

    public Observable<CurrentClockState> tick(Direction direction, TimerType timerType, ColumnType columnType) {
        if (timerType.equals(TimerType.TICK)) {
            this.mainClock = tick.apply(ColumnType.SECONDS, Direction.UP).apply(this.mainClock, timerType);
            if (columnType.equals(ColumnType.SECONDS)) {
                this.scrollSecondsClock = tick.apply(ColumnType.SECONDS, direction).apply(this.scrollSecondsClock, timerType);
            }
            if (columnType.equals(ColumnType.MINUTES)) {
                this.scrollMinutesClock = tick.apply(ColumnType.MINUTES, direction).apply(this.scrollMinutesClock, timerType);
            }
            if (columnType.equals(ColumnType.HOURS)) {
                this.scrollHoursClock = tick.apply(ColumnType.HOURS, direction).apply(this.scrollHoursClock, timerType);
            }

        }

        if (timerType.equals(TimerType.SCROLL)) {
            if (columnType.equals(ColumnType.SECONDS)) {
                this.scrollSecondsClock = tick.apply(columnType, direction).apply(this.scrollSecondsClock, timerType);
                this.mainClock = this.mainClock.withSecond(this.scrollSecondsClock.getSecond());
            }

            if (columnType.equals(ColumnType.MINUTES)) {
                this.scrollMinutesClock = tick.apply(columnType, direction).apply(this.scrollMinutesClock, timerType);
                this.mainClock = this.mainClock.withMinute(this.scrollMinutesClock.getMinute());
            }

            if (columnType.equals(ColumnType.HOURS)) {
                this.scrollHoursClock = tick.apply(columnType, direction).apply(this.scrollHoursClock, timerType);
                this.mainClock = this.mainClock.withHour(this.scrollHoursClock.getHour());
            }

            this.mainClock = tick.apply(columnType, direction.getDelta() < 0 ? Direction.DOWN : Direction.UP).apply(this.mainClock, timerType);
        }

        return Observable.just(new CurrentClockState(
                this.scrollSecondsClock.getSecond(),
                this.scrollMinutesClock.getMinute(),
                this.scrollHoursClock.getHour(),
                direction,
                shouldTick.test(this.mainClock.getSecond()) && !timerType.equals(TimerType.SCROLL),
                shouldTick.test(this.mainClock.getSecond()) && shouldTick.test(this.mainClock.getMinute()) && !timerType.equals(TimerType.SCROLL)
            )
        );
    }

    @Override
    public Map<ColumnType, List<Integer>> initialize(Direction fromDirection) {
        List<Integer> seconds = List.of(mainClock.plusSeconds(2).getSecond(), mainClock.plusSeconds(1).getSecond(), mainClock.getSecond(), mainClock.minusSeconds(1).getSecond());
        List<Integer> minutes = List.of(mainClock.plusMinutes(2).getMinute(), mainClock.plusMinutes(1).getMinute(), mainClock.getMinute(), mainClock.minusMinutes(1).getMinute());
        List<Integer> hours = List.of(mainClock.plusHours(2).getHour(), mainClock.plusHours(1).getHour(), mainClock.getHour(), mainClock.minusHours(1).getHour());
        return Map.of(ColumnType.SECONDS, seconds, ColumnType.MINUTES, minutes, ColumnType.HOURS, hours);
    }
}
