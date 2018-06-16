package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.TimerType;
import io.reactivex.Single;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.IntStream;

public class LocalTimeClock implements Clock {

    private final TimerRepository timerRepository;
    private LocalTime mainClock          = LocalTime.of(0, 0, 0);
    private LocalTime scrollSecondsClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollMinutesClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollHoursClock   = LocalTime.of(0, 0, 0);

    private Comparator<Integer> clockSort = (num1, num2) -> {
        return num1.equals(num2) ? 0 : num1 == 0 ? -1 : num1 > num2 ? -1 : 1;
    };

    private Predicate<Integer> shouldTick = clockState -> clockState == 59;

    private BiFunction<ColumnType, Integer, Function<LocalTime, LocalTime>> tick = (columnType, direction) -> {
        if (columnType.equals(ColumnType.SECONDS)) {
            return localTime -> localTime.plusSeconds(direction);
        }

        if (columnType.equals(ColumnType.MINUTES)) {
            return localTime -> localTime.plusMinutes(direction);
        }

        if (columnType.equals(ColumnType.HOURS)) {
            return localTime -> localTime.plusHours(direction);
        }

        return localTime -> localTime;
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

    public Single<CurrentClockState> tick(Direction direction, TimerType timerType, ColumnType columnType) {
        if (timerType.equals(TimerType.TICK)) {
            this.mainClock = tick.apply(ColumnType.SECONDS, Direction.UP.getDelta()).apply(this.mainClock);
            if (columnType.equals(ColumnType.SECONDS)) {
                this.scrollSecondsClock = tick.apply(ColumnType.SECONDS, direction.getDelta()).apply(this.scrollSecondsClock);
            }
            if (columnType.equals(ColumnType.MINUTES)) {
                this.scrollMinutesClock = tick.apply(ColumnType.MINUTES, direction.getDelta()).apply(this.scrollMinutesClock);
            }
            if (columnType.equals(ColumnType.HOURS)) {
                this.scrollHoursClock = tick.apply(ColumnType.HOURS, direction.getDelta()).apply(this.scrollHoursClock);
            }

        }

        if (timerType.equals(TimerType.SCROLL)) {
            if (columnType.equals(ColumnType.SECONDS)) {
                this.scrollSecondsClock = tick.apply(columnType, direction.getDelta()).apply(this.scrollSecondsClock);
                this.mainClock = this.mainClock.withSecond(this.scrollSecondsClock.getSecond());
            }

            if (columnType.equals(ColumnType.MINUTES)) {
                this.scrollMinutesClock = tick.apply(columnType, direction.getDelta()).apply(this.scrollMinutesClock);
                this.mainClock = this.mainClock.withMinute(this.scrollMinutesClock.getMinute());
            }

            if (columnType.equals(ColumnType.HOURS)) {
                this.scrollHoursClock = tick.apply(columnType, direction.getDelta()).apply(this.scrollHoursClock);
                this.mainClock = this.mainClock.withHour(this.scrollHoursClock.getHour());
            }

            this.mainClock = tick.apply(columnType, direction.getDelta() < 0 ? Direction.DOWN.getDelta() : Direction.UP.getDelta()).apply(this.mainClock);
        }

        return Single.just(new CurrentClockState(
                this.scrollSecondsClock.getSecond(),
                this.scrollMinutesClock.getMinute(),
                this.scrollHoursClock.getHour(),
                direction,
                columnType.equals(ColumnType.SECONDS),
                (shouldTick.test(this.mainClock.getSecond()) && !timerType.equals(TimerType.SCROLL)) || (timerType.equals(TimerType.SCROLL) && columnType.equals(ColumnType.MINUTES)),
                (shouldTick.test(this.mainClock.getSecond()) && shouldTick.test(this.mainClock.getMinute()) && !timerType.equals(TimerType.SCROLL)) || (timerType.equals(TimerType.SCROLL) && columnType.equals(ColumnType.HOURS))
            )
        );
    }

    @Override
    public Map<ColumnType, ArrayList<Integer>> initialize(Direction fromDirection) {
        return IntStream.rangeClosed(0, 3).mapToObj(index -> {
            int second = this.tick.apply(ColumnType.SECONDS, index - 1).apply(this.mainClock).getSecond();
            int minute = this.tick.apply(ColumnType.MINUTES, index - 1).apply(this.mainClock).getMinute();
            int hour = this.tick.apply(ColumnType.HOURS, index - 1).apply(this.mainClock).getHour();
            return Map.of(ColumnType.SECONDS, second, ColumnType.MINUTES, minute, ColumnType.HOURS, hour);
        }).collect(Collector.of(
                () -> Map.of(ColumnType.SECONDS, new ArrayList<Integer>(), ColumnType.MINUTES, new ArrayList<Integer>(), ColumnType.HOURS, new ArrayList<Integer>()),
                (mapResultContainer, mapOfValues) -> {
                    mapResultContainer.get(ColumnType.SECONDS).add(mapOfValues.get(ColumnType.SECONDS));
                    mapResultContainer.get(ColumnType.MINUTES).add(mapOfValues.get(ColumnType.MINUTES));
                    mapResultContainer.get(ColumnType.HOURS).add(mapOfValues.get(ColumnType.HOURS));
                },
                (result1, result2) -> {
                    result1.putAll(result2);
                    return result1;
                },
                (result) -> {
                    result.get(ColumnType.SECONDS).sort(clockSort);
                    result.get(ColumnType.MINUTES).sort(clockSort);
                    result.get(ColumnType.HOURS).sort(clockSort);
                    return result;
                })
        );
    }
}
