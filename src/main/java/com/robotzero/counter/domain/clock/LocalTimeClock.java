package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
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
                return LocalTime::plusSeconds;
            }
            return LocalTime::minusSeconds;
        }

        if (shouldTick.test(this.mainClock.getSecond())) {
            if (columnType.equals(ColumnType.MINUTES)) {
                if (predicate.test(currentDelta)) {
                    return LocalTime::plusMinutes;
                }
                return LocalTime::minusMinutes;
            }
        } else {
            return (localTime, integer) -> localTime;
        }

        if (shouldTick.test(this.mainClock.getMinute())) {
            if (predicate.test(currentDelta)) {
                return LocalTime::plusHours;
            }
            return LocalTime::minusHours;
        } else {
            return (localTime, integer) -> localTime;
        }
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
        this.scrollSecondsClock = this.scrollSecondsClock.withSecond(mainClock.getSecond() - 1);
        this.scrollMinutesClock = this.scrollMinutesClock.withMinute(mainClock.getMinute() - 1);
        this.scrollHoursClock = this.scrollHoursClock.withHour(mainClock.getHour() - 1);
    }

    public Observable<CurrentClockState> tick(Direction direction) {
        this.mainClock = tick.apply(isDeltaGreaterThan, direction.getDelta(), ColumnType.SECONDS).apply(this.mainClock, 1);
        this.scrollSecondsClock = tick.apply(isDeltaGreaterThan, direction.getDelta(), ColumnType.SECONDS).apply(this.scrollSecondsClock, Math.abs(direction.getDelta()));
        this.scrollMinutesClock = tick.apply(isDeltaGreaterThan, direction.getDelta(), ColumnType.MINUTES).apply(this.scrollMinutesClock, Math.abs(direction.getDelta()));
        this.scrollHoursClock = tick.apply(isDeltaGreaterThan, direction.getDelta(), ColumnType.HOURS).apply(this.scrollHoursClock, Math.abs(direction.getDelta()));

        return Observable.just(new CurrentClockState(
                this.scrollSecondsClock.getSecond(),
                this.scrollMinutesClock.getMinute(),
                this.scrollHoursClock.getHour(),
                shouldTick.test(this.mainClock.getSecond()),
                shouldTick.test(this.mainClock.getSecond()) && shouldTick.test(this.mainClock.getMinute())
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
