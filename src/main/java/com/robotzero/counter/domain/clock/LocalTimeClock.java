package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ChangeCell;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.TimerType;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.IntStream;

public class LocalTimeClock implements Clock {

    private final ClockRepository clockRepository;
    private final TimerRepository timerRepository;
    private final PublishSubject<CurrentClockState> currectClockStateObservable;
    private final Map<TimerType, ClockMode> clockmodes;

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

    public LocalTimeClock(ClockRepository clockRepository, TimerRepository timerRepository, Map<TimerType, ClockMode> clockModes, PublishSubject<CurrentClockState> currentClockStateObservable) {
        this.clockRepository = clockRepository;
        this.timerRepository = timerRepository;
        this.clockmodes = clockModes;
        this.currectClockStateObservable = currentClockStateObservable;
    }

    @PostConstruct
    public void initialize() {
        this.clockRepository.initialize(Optional.ofNullable(timerRepository.selectLatest()).orElseGet(() -> {
            com.robotzero.counter.entity.Clock savedTimer = new com.robotzero.counter.entity.Clock();
            savedTimer.setSavedTimer(LocalTime.of(22, 1, 10));
            return savedTimer;
        }).getSavedTimer());
    }

    public Single<CurrentClockState> tick(Direction direction, TimerType timerType, ColumnType columnType, List<Flowable<ChangeCell>> cells) {
        clockmodes.get(timerType).applyNewClockState(tick, columnType, direction);
        LocalTime mainClock = this.clockRepository.get(ColumnType.MAIN);
        LocalTime scrollSecondsClock = this.clockRepository.get(ColumnType.SECONDS);
        LocalTime scrollMinutesClock = this.clockRepository.get(ColumnType.MINUTES);
        LocalTime scrollHoursClock = this.clockRepository.get(ColumnType.HOURS);

        currectClockStateObservable.onNext(new CurrentClockState(
                scrollSecondsClock.getSecond(),
                scrollMinutesClock.getMinute(),
                scrollHoursClock.getHour(),
                direction,
                columnType.equals(ColumnType.SECONDS),
                (shouldTick.test(mainClock.getSecond()) && timerType.equals(TimerType.TICK)) || (!timerType.equals(TimerType.TICK) && columnType.equals(ColumnType.MINUTES)),
                (shouldTick.test(mainClock.getSecond()) && shouldTick.test(mainClock.getMinute()) && timerType.equals(TimerType.TICK)) || (!timerType.equals(TimerType.TICK) && columnType.equals(ColumnType.HOURS))
        ));

        return Single.just(new CurrentClockState(
                scrollSecondsClock.getSecond(),
                scrollMinutesClock.getMinute(),
                scrollHoursClock.getHour(),
                direction,
                columnType.equals(ColumnType.SECONDS),
                (shouldTick.test(mainClock.getSecond()) && timerType.equals(TimerType.TICK)) || (!timerType.equals(TimerType.TICK) && columnType.equals(ColumnType.MINUTES)),
                (shouldTick.test(mainClock.getSecond()) && shouldTick.test(mainClock.getMinute()) && timerType.equals(TimerType.TICK)) || (!timerType.equals(TimerType.TICK) && columnType.equals(ColumnType.HOURS))
            )
        );
    }

    @Override
    public Map<ColumnType, ArrayList<Integer>> initialize(Direction fromDirection) {
        LocalTime mainClock = this.clockRepository.get(ColumnType.MAIN);
        return IntStream.rangeClosed(0, 3).mapToObj(index -> {
            int second = this.tick.apply(ColumnType.SECONDS, index - 1).apply(mainClock).getSecond();
            int minute = this.tick.apply(ColumnType.MINUTES, index - 1).apply(mainClock).getMinute();
            int hour = this.tick.apply(ColumnType.HOURS, index - 1).apply(mainClock).getHour();
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

    @Override
    public void calculateResetDifference(LocalTime calculateTo) {

    }
}
