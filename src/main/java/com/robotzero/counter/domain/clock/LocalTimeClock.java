package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.TimerType;
import io.reactivex.Observable;
import org.reactfx.util.TriFunction;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class LocalTimeClock implements Clock {

    private final TimerRepository timerRepository;
    private LocalTime mainClock          = LocalTime.of(0, 0, 0);
    private LocalTime scrollSecondsClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollMinutesClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollHoursClock   = LocalTime.of(0, 0, 0);

    private Predicate<Integer> isDeltaGreaterThan = delta -> delta > 0;
    private Predicate<Integer> shouldTick = clockState -> clockState == 0;

    private TriFunction<Predicate<Integer>, Integer, ColumnType, BiFunction<LocalTime, Integer, LocalTime>> tick = (predicate, currentDelta, columnType) -> {
        if (columnType.equals(ColumnType.SECONDS)) {
            if (predicate.test(currentDelta)) {
                System.out.println("MINUS");
                return LocalTime::plusSeconds;
            }
            return (localTime, integer) -> localTime.minusSeconds(integer);
        }

        if (shouldTick.test(this.mainClock.getSecond()) || columnType.equals(ColumnType.MINUTES)) {
             if (predicate.test(currentDelta)) {
                 return LocalTime::plusMinutes;
             }
             return (localTime, integer) -> localTime.minusMinutes(integer);
        }

        if (shouldTick.test(this.mainClock.getMinute()) || columnType.equals(ColumnType.HOURS)) {
            if (predicate.test(currentDelta)) {
                return LocalTime::plusHours;
            }
            return (localtime, integer) -> localtime.minusHours(integer);
        }

        return (localTime, integer) -> localTime;
    };

    private Function<Integer, Predicate<Integer>> isGreaterThan = pivot -> {
        return candidate -> candidate > pivot;
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
        this.scrollMinutesClock = this.scrollMinutesClock.withMinute(mainClock.getMinute() - 1);
        this.scrollHoursClock = this.scrollHoursClock.withHour(mainClock.getHour() - 1);
    }

    public Observable<CurrentClockState> tick(Direction direction, TimerType timerType, ColumnType columnType) {
        if (timerType.equals(TimerType.TICK)) {
            int multiplier = this.scrollSecondsClock.getSecond() == this.mainClock.getSecond() ? 2 : 1;
            this.scrollSecondsClock = tick.apply(isDeltaGreaterThan, direction.getDelta(), ColumnType.SECONDS).apply(this.scrollSecondsClock, multiplier * Math.abs(direction.getDelta()));
            this.scrollMinutesClock = tick.apply(isDeltaGreaterThan, direction.getDelta(), ColumnType.MINUTES).apply(this.scrollMinutesClock, multiplier * Math.abs(direction.getDelta()));
            this.scrollHoursClock = tick.apply(isDeltaGreaterThan, direction.getDelta(), ColumnType.HOURS).apply(this.scrollHoursClock, multiplier * Math.abs(direction.getDelta()));
            this.mainClock = tick.apply(isDeltaGreaterThan, direction.getDelta(), ColumnType.SECONDS).apply(this.mainClock, 1);
        }

        if (timerType.equals(TimerType.SCROLL)) {
            if (columnType.equals(ColumnType.SECONDS)) {
                if (this.scrollSecondsClock.getSecond() == this.mainClock.getSecond()) {
                    this.scrollSecondsClock = tick.apply(isDeltaGreaterThan, direction.getDelta(), ColumnType.SECONDS).apply(this.scrollSecondsClock, 2 * Math.abs(direction.getDelta()));
                } else {
                    this.scrollSecondsClock = tick.apply(isDeltaGreaterThan, direction.getDelta(), ColumnType.SECONDS).apply(this.scrollSecondsClock, Math.abs(direction.getDelta()));
                }
//                this.mainClock = this.mainClock.withSecond(this.scrollSecondsClock.getSecond());
            }

            if (columnType.equals(ColumnType.MINUTES)) {
                this.scrollMinutesClock = tick.apply(isDeltaGreaterThan, direction.getDelta(), ColumnType.MINUTES).apply(this.scrollMinutesClock, Math.abs(direction.getDelta()));
//                this.mainClock = this.mainClock.withMinute(this.scrollMinutesClock.getMinute());
            }

            if (columnType.equals(ColumnType.HOURS)) {
                this.scrollHoursClock = tick.apply(isDeltaGreaterThan, direction.getDelta(), ColumnType.HOURS).apply(this.scrollHoursClock, Math.abs(direction.getDelta()));
//                this.mainClock = this.mainClock.withHour(this.scrollHoursClock.getHour());
            }
            this.mainClock = tick.apply(isDeltaGreaterThan, direction.getDelta(), columnType).apply(this.mainClock, Math.abs(direction.getDelta()));
        }

        return Observable.just(new CurrentClockState(
                this.scrollSecondsClock.getSecond(),
                this.scrollMinutesClock.getMinute(),
                this.scrollHoursClock.getHour(),
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
