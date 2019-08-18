package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.event.action.TickAction;
import com.robotzero.counter.service.DirectionService;
import com.robotzero.counter.service.LocationService;
import io.reactivex.Observable;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
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

    private Function<Optional<ColumnType>, Function<Optional<ColumnType>, Function<Optional<ColumnType>, Set<ColumnType>>>> columnTypeToTickCurried = (columnType -> {
       Set<ColumnType> columnTypes = new HashSet<>();
       return (columnType1 -> {
           columnType1.ifPresent(c -> {
               columnTypes.add(c);
           });
           return  (columnType2 -> {
               columnType2.ifPresent(c -> {
                   columnTypes.add(c);
               });
               return columnTypes;
           });
       });
    });

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

    public List<CellState> blah(TickAction action, Set<ColumnType> tickRequestForColumns) {
        return tickRequestForColumns.parallelStream().map(columnType -> {
            Deque<CellState> updatedCellState = cellStateRepository.getAll(columnType).stream().map(cellState -> {
                Location newLocation = locationService.calculate(action.getDelta(), cellState.getCurrentLocation().getToY());
                Direction directionSeconds = directionService.calculateDirection(columnType, cellState.getCurrentDirection(), action.getDelta());
                return cellState.createNew(newLocation, directionSeconds.getDirectionType(), cellState.getCurrentDirection());
            }).collect(Collectors.toCollection(ConcurrentLinkedDeque::new));
            cellStateRepository.save(columnType, updatedCellState);
            CellState top = this.cellStateRepository.get(columnType, (repo) -> repo.peekFirst());
            CellState bottom = this.cellStateRepository.get(columnType, (repo) -> repo.peekLast());
            CellState changeableCellState = this.changeableStates.stream().map(state -> {
                return state.getMoveCellStatesFunction(top, bottom);
            }).filter(Optional::isPresent).findFirst().flatMap(optional -> optional).map(changeableCellFunc -> changeableCellFunc.apply(this.cellStateRepository.getAll(columnType))).orElseThrow(() -> new RuntimeException("NAH"));
            clockmodes.get(action.getTimerType()).applyNewClockState(tick, columnType,  changeableCellState.getCurrentDirection());
            return changeableCellState;
        }).collect(Collectors.toList());

    }

    public Observable<CurrentClockState> tick(TickAction action) {
        //@TODO add action.getColumnType first.

        Function<Optional<ColumnType>, Function<Optional<ColumnType>, Set<ColumnType>>> curriedPass1 = Optional.of(shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getSecond(), action.getTimerType()))
                .filter(bool -> bool)
                .stream().map(ignored -> ColumnType.MINUTES).map(columnType -> columnTypeToTickCurried.apply(Optional.of(columnType))).findAny().orElseGet(() -> columnTypeToTickCurried.apply(Optional.empty()));

        Function<Optional<ColumnType>, Set<ColumnType>> curriedPass2 = Optional.of(shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getMinute(), action.getTimerType()) && shouldTick.test(this.clockRepository.get(ColumnType.MAIN).getSecond(), action.getTimerType()))
                .filter(bool -> bool)
                .stream().map(ignored -> ColumnType.HOURS).map(columnType -> curriedPass1.apply(Optional.of(columnType))).findAny().orElseGet(() -> curriedPass1.apply(Optional.empty()));

        Set<ColumnType> columnTypesToTick = curriedPass2.apply(Optional.of(action.getColumnType()));

        List<CellState> cellStatesSoFar = blah(action, columnTypesToTick);

        return Observable.just(new CurrentClockState(
                Map.of(
                        ColumnType.SECONDS, this.clockRepository.get(ColumnType.SECONDS).getSecond(),
                        ColumnType.MINUTES, this.clockRepository.get(ColumnType.MINUTES).getMinute(),
                        ColumnType.HOURS, this.clockRepository.get(ColumnType.HOURS).getHour()),
                cellStatesSoFar,
                this.clockRepository.get(ColumnType.MAIN)
        ));
    }

    @Override
    public Map<ColumnType, ArrayList<Integer>> initializeLabels(DirectionType fromDirection) {
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
