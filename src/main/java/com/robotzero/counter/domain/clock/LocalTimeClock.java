package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.event.action.TickAction;
import com.robotzero.counter.service.DirectionService;
import com.robotzero.counter.service.LocationService;
import io.reactivex.Observable;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LocalTimeClock implements Clock {

    private final ClockRepository clockRepository;
    private final TimerRepository timerRepository;
    private final CellStateRepository cellStateRepository;
    private final DirectionService directionService;
    private final LocationService locationService;
    private final Map<TimerType, ClockMode> clockmodes;
    private final List<ChangeableState> changeableStates;
    private int count;

    private Comparator<Integer> clockSort = (num1, num2) -> {
        return num1.equals(num2) ? 0 : num1 == 0 ? -1 : num1 > num2 ? -1 : 1;
    };

    private BiPredicate<Integer, TimerType> shouldTick = (clockState, timerType) -> clockState == 0 && timerType == TimerType.TICK;

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
            DirectionService directionService,
            List<ChangeableState> changeableStates
    ) {
        this.clockRepository = clockRepository;
        this.timerRepository = timerRepository;
        this.cellStateRepository = cellStateRepository;
        this.locationService = locationService;
        this.clockmodes = clockModes;
        this.directionService = directionService;
        this.changeableStates = changeableStates;
    }

    @PostConstruct
    public void initialize() {
        this.clockRepository.initialize(Optional.ofNullable(timerRepository.selectLatest()).orElseGet(() -> {
            com.robotzero.counter.entity.Clock savedTimer = new com.robotzero.counter.entity.Clock();
            savedTimer.setSavedTimer(LocalTime.of(22, 1, 10));
            return savedTimer;
        }).getSavedTimer());
    }

    public List<CellState> blah(TickAction action, Set<ColumnType> tickRequestForColumns) {
        return tickRequestForColumns.stream().map(columnType -> {
            ArrayDeque<CellState> updatedCellState = cellStateRepository.getAll(columnType).stream().map(cellState -> {
                Location newLocation = locationService.calculate(action.getDelta(), cellState.getCurrentLocation().getToY());
                Direction directionSeconds = directionService.calculateDirection(columnType, cellState.getCurrentDirection(), action.getDelta());
                return cellState.createNew(newLocation, directionSeconds.getDirectionType(), cellState.getCurrentDirection());
            }).collect(Collectors.toCollection(ArrayDeque::new));
            cellStateRepository.save(columnType, updatedCellState);
            CellState top = this.cellStateRepository.get(columnType, (repo) -> repo.peekFirst());
            CellState bottom = this.cellStateRepository.get(columnType, (repo) -> repo.peekLast());
            CellState changeableCellState = this.changeableStates.stream().map(state -> {
                return state.moveCellStates(top).or(() -> state.moveCellStates(bottom));
            }).filter(Optional::isPresent).map(changeableCellFunc -> changeableCellFunc.get().apply(this.cellStateRepository.getAll(columnType))).findFirst().orElseThrow(() -> new RuntimeException("NAH"));
            clockmodes.get(action.getTimerType()).applyNewClockState(tick, columnType,  changeableCellState.getCurrentDirection());
            return changeableCellState;
        }).collect(Collectors.toList());

    }

    public Observable<CurrentClockState> tick(TickAction action) {

        Set<ColumnType> minutes = Optional.of(shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getSecond(), action.getTimerType()))
                .filter(bool -> bool)
                .stream().map(ignore -> ColumnType.MINUTES).collect(Collectors.toSet());

        Set<ColumnType> hours = Optional.of(shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getMinute(), action.getTimerType()) && shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getSecond(), action.getTimerType()))
                .filter(bool -> bool)
                .stream().map(ignore -> ColumnType.HOURS).collect(Collectors.toSet());

        minutes.add(action.getColumnType());
        minutes.addAll(hours);
        List<CellState> cellStatesSoFar = blah(action, minutes);

        return Observable.just(new CurrentClockState(
                this.clockRepository.get(ColumnType.SECONDS).getSecond(),
                this.clockRepository.get(ColumnType.MINUTES).getMinute(),
                this.clockRepository.get(ColumnType.HOURS).getHour(),
                cellStatesSoFar,
                this.clockRepository.get(ColumnType.MAIN)
        ));
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
