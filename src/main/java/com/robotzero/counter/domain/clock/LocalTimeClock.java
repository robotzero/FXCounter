package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.event.action.TickAction;
import com.robotzero.counter.service.DirectionService;
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
    private final DirectionService directionService;
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

    public LocalTimeClock(
            ClockRepository clockRepository,
            TimerRepository timerRepository,
            Map<TimerType, ClockMode> clockModes,
            PublishSubject<CurrentClockState> currentClockStateObservable,
            DirectionService directionService
    ) {
        this.clockRepository = clockRepository;
        this.timerRepository = timerRepository;
        this.clockmodes = clockModes;
        this.currectClockStateObservable = currentClockStateObservable;
        this.directionService = directionService;
    }

    @PostConstruct
    public void initialize() {
        this.clockRepository.initialize(Optional.ofNullable(timerRepository.selectLatest()).orElseGet(() -> {
            com.robotzero.counter.entity.Clock savedTimer = new com.robotzero.counter.entity.Clock();
            savedTimer.setSavedTimer(LocalTime.of(22, 1, 10));
            return savedTimer;
        }).getSavedTimer());
    }

    public Single<CurrentClockState> tick(TickAction action, List<ChangeCell> cells) {
        Map<ColumnType, Direction> directions = cells.stream().map(cell -> {
            if (cell.getColumnType() == ColumnType.SECONDS) {
                Direction directionSeconds = directionService.calculateDirection(cell.getTranslateY(), action.getDelta(), cell.getColumnType());
                clockmodes.get(action.getTimerType()).applyNewClockState(tick, cell.getColumnType(), directionSeconds);
                return Map.of(cell.getColumnType(), directionSeconds);
            }
            if (shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getSecond()) && action.getTimerType() == TimerType.TICK && cell.getColumnType() == ColumnType.MINUTES) {
                Direction directionMinutes = directionService.calculateDirection(cell.getTranslateY(), action.getDelta(), cell.getColumnType());
                clockmodes.get(action.getTimerType()).applyNewClockState(tick, action.getColumnType(), directionMinutes);
                return Map.of(cell.getColumnType(), directionMinutes);
            }

            if ((shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getMinute()) && action.getTimerType() == TimerType.TICK) && cell.getColumnType() == ColumnType.HOURS) {
                Direction directionHours = directionService.calculateDirection(cell.getTranslateY(), action.getDelta(), cell.getColumnType());
                clockmodes.get(action.getTimerType()).applyNewClockState(tick, ColumnType.HOURS, directionHours);
                return Map.of(cell.getColumnType(), directionHours);
            }
            return Map.of(cell.getColumnType(), Direction.VOID);
        }).collect(HashMap::new, (previous, next) -> {
            previous.putIfAbsent(ColumnType.SECONDS, next.get(ColumnType.SECONDS));
            previous.putIfAbsent(ColumnType.MINUTES, next.get(ColumnType.MINUTES));
            previous.putIfAbsent(ColumnType.HOURS, next.get(ColumnType.HOURS));
        }, (b, c) -> {
            System.out.println(b);
            System.out.println(c);
        });

        LocalTime mainClock = this.clockRepository.get(ColumnType.MAIN);
        TimerType timerType = action.getTimerType();
        ColumnType columnType = action.getColumnType();

        currectClockStateObservable.onNext(new CurrentClockState(
                this.clockRepository.get(ColumnType.SECONDS).getSecond(),
                this.clockRepository.get(ColumnType.MINUTES).getMinute(),
                this.clockRepository.get(ColumnType.HOURS).getHour(),
                directions.get(ColumnType.SECONDS),
                directions.get(ColumnType.MINUTES),
                directions.get(ColumnType.HOURS),
                columnType == ColumnType.SECONDS,
                (shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getSecond()) && timerType == TimerType.TICK) || (timerType != TimerType.TICK && columnType == ColumnType.MINUTES),
                (shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getSecond()) && shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getMinute()) && timerType == TimerType.TICK) || (timerType != TimerType.TICK && columnType == ColumnType.HOURS)
        ));

        return Single.just(new CurrentClockState(
                this.clockRepository.get(ColumnType.SECONDS).getSecond(),
                this.clockRepository.get(ColumnType.MINUTES).getMinute(),
                this.clockRepository.get(ColumnType.HOURS).getHour(),
                directions.get(ColumnType.SECONDS),
                directions.get(ColumnType.MINUTES),
                directions.get(ColumnType.HOURS),
                columnType == ColumnType.SECONDS,
                (shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getSecond()) && timerType == TimerType.TICK) || (timerType != TimerType.TICK && columnType == ColumnType.MINUTES),
                (shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getSecond()) && shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getMinute()) && timerType == TimerType.TICK) || (timerType != TimerType.TICK && columnType == ColumnType.HOURS)
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
}
