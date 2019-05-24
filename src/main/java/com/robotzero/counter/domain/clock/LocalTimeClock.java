package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.event.action.TickAction;
import com.robotzero.counter.service.DirectionService;
import com.robotzero.counter.service.LocationService;
import io.reactivex.Completable;
import io.reactivex.subjects.Subject;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LocalTimeClock implements Clock {

    private final ClockRepository clockRepository;
    private final TimerRepository timerRepository;
    private final CellStateRepository cellStateRepository;
    private final Subject<CurrentClockState> currectClockStateObservable;
    private final DirectionService directionService;
    private final LocationService locationService;
    private final Map<TimerType, ClockMode> clockmodes;
    private int count;

    private Comparator<Integer> clockSort = (num1, num2) -> {
        return num1.equals(num2) ? 0 : num1 == 0 ? -1 : num1 > num2 ? -1 : 1;
    };

    private Predicate<Integer> shouldTick = clockState -> clockState == 59;

    private BiFunction<ColumnType, Integer, Function<LocalTime, LocalTime>> tick = (columnType, direction) -> {
        if (columnType == ColumnType.SECONDS || columnType == ColumnType.MAIN) {
            return localTime -> localTime.plusSeconds(direction);
        }

        if (columnType == ColumnType.MINUTES) {
            return localTime -> localTime.plusMinutes(direction);
        }

        if (columnType == ColumnType.HOURS) {
            return localTime -> localTime.plusHours(direction);
        }

        return localTime -> localTime;
    };

    private final long MIN = ChronoUnit.MINUTES.getDuration().toMinutes();
    private final long HR = ChronoUnit.HOURS.getDuration().toHours();

    public LocalTimeClock(
            ClockRepository clockRepository,
            TimerRepository timerRepository,
            CellStateRepository cellStateRepository,
            LocationService locationService,
            Map<TimerType, ClockMode> clockModes,
            Subject<CurrentClockState> currentClockStateObservable,
            DirectionService directionService
    ) {
        this.clockRepository = clockRepository;
        this.timerRepository = timerRepository;
        this.cellStateRepository = cellStateRepository;
        this.locationService = locationService;
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

    public Completable tick(TickAction action) {
        return Completable.fromRunnable(() -> {
            List<CellState> cellStates = this.cellStateRepository.update(locationService, directionService, action.getColumnType(), action.getDelta());
            List<Direction> directions = cellStates.stream().map(cellState -> {
                if (cellState.getColumnType() == ColumnType.SECONDS && action.getColumnType() == ColumnType.SECONDS) {
                    Direction directionSeconds = directionService.calculateDirection(cellState, action.getDelta());
                    clockmodes.get(action.getTimerType()).applyNewClockState(tick, cellState.getColumnType(), directionSeconds.getDirectionType());
//                    cellStateRepository.update(directionSeconds, cellState.getColumnType());
                    return directionSeconds;
                }

                if ((shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getSecond()) && action.getTimerType() == TimerType.TICK && cellState.getColumnType() == ColumnType.MINUTES) || (action.getColumnType() == ColumnType.MINUTES && cellState.getColumnType() == ColumnType.MINUTES)) {
//                    cellStateRepository.update(locationService, directionMinutes, cellState.getColumnType());
                    Direction directionMinutes = directionService.calculateDirection(cellState, action.getDelta());
                    clockmodes.get(action.getTimerType()).applyNewClockState(tick, cellState.getColumnType(), directionMinutes.getDirectionType());
                    return directionMinutes;
                }

                if ((shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getMinute()) && shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getSecond()) && action.getTimerType() == TimerType.TICK && cellState.getColumnType() == ColumnType.HOURS) || (action.getColumnType() == ColumnType.HOURS && cellState.getColumnType() == ColumnType.HOURS)) {
                    Direction directionHours = directionService.calculateDirection(cellState, action.getDelta());
                    clockmodes.get(action.getTimerType()).applyNewClockState(tick, cellState.getColumnType(), directionHours.getDirectionType());
//                    cellStateRepository.update(locationService, directionHours, cellState.getColumnType());
                    return directionHours;
                }

                return new Direction(cellState.getColumnType(), DirectionType.VOID);
            }).collect(Collectors.toList());

            currectClockStateObservable.onNext(new CurrentClockState(
                    this.clockRepository.get(ColumnType.SECONDS).getSecond(),
                    this.clockRepository.get(ColumnType.MINUTES).getMinute(),
                    this.clockRepository.get(ColumnType.HOURS).getHour(),
                    directions.stream().filter(direction -> direction.getColumnType() == ColumnType.SECONDS).findFirst().get(),
                    directions.stream().filter(direction -> direction.getColumnType() == ColumnType.SECONDS).findFirst().get(),
                    directions.stream().filter(direction -> direction.getColumnType() == ColumnType.SECONDS).findFirst().get(),
                    action.getColumnType() == ColumnType.SECONDS,
                    directions.stream().filter(direction -> direction.getColumnType() == ColumnType.SECONDS).anyMatch(direction -> direction.getDirectionType() != DirectionType.VOID),
                    directions.stream().filter(direction -> direction.getColumnType() == ColumnType.SECONDS).anyMatch(direction -> direction.getDirectionType() != DirectionType.VOID),
                    cellStates,
                    this.clockRepository.get(ColumnType.MAIN)
            ));
        });
//        return Completable.complete();
    }

    @Override
    public Map<ColumnType, ArrayList<Integer>> initialize(DirectionType fromDirection) {
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
