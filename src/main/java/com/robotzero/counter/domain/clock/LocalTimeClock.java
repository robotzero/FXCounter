package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.event.action.TickAction;
import com.robotzero.counter.service.DirectionService;
import com.robotzero.counter.service.LocationMemoizerKey;
import com.robotzero.counter.service.LocationService;
import io.reactivex.Observable;

import java.time.LocalTime;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
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

    Function<ChronoField, TemporalQuery<Boolean>> shouldTickSpecificField = (chronoField -> {
        return temporal -> {
            LocalTime queried = LocalTime.from(temporal);
            return queried.get(chronoField) == 0;
        };
    });

    private BiFunction<Integer, ChronoUnit, Function<LocalTime, LocalTime>> tick = (direction, chronoUnit) -> {
        return localTime -> localTime.plus(direction, chronoUnit);
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

    public void initializeTime() {
        this.clockRepository.initialize(Optional.ofNullable(timerRepository.selectLatest()).orElseGet(() -> {
            com.robotzero.counter.entity.Clock savedTimer = new com.robotzero.counter.entity.Clock();
            savedTimer.setSavedTimer(LocalTime.of(22, 1, 10));
            return savedTimer;
        }).getSavedTimer());
    }

    public List<CellState> blah(TickAction action) {
        return action.getTickData().parallelStream().map(tick -> {
            Deque<CellState> updatedCellState = cellStateRepository.getAll(tick.getColumnType()).stream().map(cellState -> {
                Location newLocation = locationService.calculate(new LocationMemoizerKey(action.getDelta(), cellState.getCurrentLocation().getToY(), tick.getColumnType()));
                Direction directionSeconds = directionService.calculateDirection(tick.getColumnType(), cellState.getCurrentDirection(), action.getDelta());
                return cellState.createNew(newLocation, directionSeconds.getDirectionType(), cellState.getCurrentDirection());
            }).collect(Collectors.toCollection(ConcurrentLinkedDeque::new));
            cellStateRepository.save(tick.getColumnType(), updatedCellState);
            CellState top = this.cellStateRepository.get(tick.getColumnType(), (repo) -> repo.peekFirst());
            CellState bottom = this.cellStateRepository.get(tick.getColumnType(), (repo) -> repo.peekLast());
            CellState changeableCellState = this.changeableStates.stream().map(state -> {
                return state.getMoveCellStatesFunction(top, bottom);
            }).filter(Optional::isPresent).findFirst().flatMap(optional -> optional).map(changeableCellFunc -> changeableCellFunc.apply(this.cellStateRepository.getAll(tick.getColumnType()))).orElseThrow(() -> new RuntimeException("NAH"));
            clockmodes.get(action.getTimerType()).applyNewClockState(this.tick, tick, changeableCellState.getCurrentDirection());
            return changeableCellState;
        }).collect(Collectors.toList());
    }

    public Observable<CurrentClockState> tick(TickAction action) {
        TickAction enrichedTickAction = List.of(action, action)
                .stream()
                .filter(ignored -> action.getTimerType() == TimerType.TICK)
                .reduce((current, next) -> {
                    if (shouldTickSpecificField.apply(ChronoField.SECOND_OF_MINUTE).queryFrom(this.clockRepository.get(ColumnType.MAIN))) {
                        return current.with(ColumnType.MINUTES, ChronoField.MINUTE_OF_HOUR, ChronoUnit.MINUTES);
                    }
                    if (shouldTickSpecificField.apply(ChronoField.SECOND_OF_MINUTE).queryFrom(this.clockRepository.get(ColumnType.MAIN)) && shouldTickSpecificField.apply(ChronoField.MINUTE_OF_HOUR).queryFrom(this.clockRepository.get(ColumnType.MAIN))) {
                        return current.with(ColumnType.HOURS, ChronoField.HOUR_OF_DAY, ChronoUnit.HOURS);
                    }
                    return current;
                }).orElse(action);

        List<CellState> cellStatesSoFar = blah(enrichedTickAction);

        return Observable.just(new CurrentClockState(
                Map.of(
                        ColumnType.SECONDS, this.clockRepository.get(ColumnType.SECONDS).getSecond(),
                        ColumnType.MINUTES, this.clockRepository.get(ColumnType.MINUTES).getMinute(),
                        ColumnType.HOURS, this.clockRepository.get(ColumnType.HOURS).getHour()),
                cellStatesSoFar
        ));
    }

    @Override
    public Map<ColumnType, ArrayList<Integer>> initializeLabels(DirectionType fromDirection) {
        LocalTime mainClock = this.clockRepository.get(ColumnType.MAIN);
        return IntStream.rangeClosed(0, 3).mapToObj(index -> {
            int second = this.tick.apply(index - 1, ChronoUnit.SECONDS).apply(mainClock).getSecond();
            int minute = this.tick.apply(index - 1, ChronoUnit.MINUTES).apply(mainClock).getMinute();
            int hour = this.tick.apply(index - 1, ChronoUnit.HOURS).apply(mainClock).getHour();
            return Map.of(ColumnType.SECONDS, second, ColumnType.MINUTES, minute, ColumnType.HOURS, hour);
        }).collect(Collector.of(
                () -> {
                    return Map.of(ColumnType.SECONDS, new ArrayList<Integer>(), ColumnType.MINUTES, new ArrayList<Integer>(), ColumnType.HOURS, new ArrayList<Integer>());
                },
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
                    result.forEach((key, value) -> value.sort(Collections.reverseOrder()));
                    return result;
                })
        );
    }
}
