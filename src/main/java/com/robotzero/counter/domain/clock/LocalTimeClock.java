package com.robotzero.counter.domain.clock;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

import com.robotzero.counter.domain.CellState;
import com.robotzero.counter.domain.CellStatePosition;
import com.robotzero.counter.domain.CellStateRepository;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.Location;
import com.robotzero.counter.domain.TimerType;
import com.robotzero.counter.event.action.TickAction;
import com.robotzero.counter.helper.InitViewCellStateSorter;
import com.robotzero.counter.service.DirectionService;
import com.robotzero.counter.service.LocationMemoizerKey;
import com.robotzero.counter.service.LocationService;
import io.reactivex.rxjava3.core.Observable;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalQuery;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
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

  Function<ChronoField, TemporalQuery<Boolean>> shouldTickSpecificField =
    (
      chronoField -> {
        return temporal -> {
          LocalTime queried = LocalTime.from(temporal);
          return queried.get(chronoField) == 0;
        };
      }
    );

  private BiFunction<Integer, ChronoUnit, Function<LocalTime, LocalTime>> tick = (direction, chronoUnit) -> {
    return localTime -> localTime.plus(direction, chronoUnit);
  };

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
    this.clockRepository.initialize(timerRepository.selectLatest().getSavedTimer());
  }

  private List<CellState> produceCellsForTextChange(final TickAction action) {
    return action
      .getTickData()
      .parallelStream()
      .map(
        tick -> {
          // Grab the first cell from the list, all have the same direction.
          Direction newDirection = directionService.calculateDirection(
            tick.getColumnType(),
            cellStateRepository
              .getColumn(tick.getColumnType())
              .getCellStates()
              .entrySet()
              .stream()
              .findAny()
              .map(cell -> cell.getValue().getCurrentDirection())
              .orElseThrow(),
            action.getDelta()
          );
          cellStateRepository
            .getColumn(tick.getColumnType())
            .getCellStates()
            .forEach(
              (key1, cell) -> {
                Location newLocation = locationService.calculate(
                  new LocationMemoizerKey(action.getDelta(), cell.getCurrentLocation().getToY(), tick.getColumnType())
                );
                CellStatePosition isChangeable =
                  this.changeableStates.stream()
                    .filter(
                      state -> {
                        return state.supports(newLocation.getFromY(), cell.getCurrentDirection());
                      }
                    )
                    .map(state -> CellStatePosition.CHANGEABLE)
                    .findAny()
                    .orElseGet(() -> CellStatePosition.NONCHANGABLE);
                final var newCellState = cell.createNew(
                  newLocation,
                  newDirection.getDirectionType(),
                  cell.getCurrentDirection(),
                  isChangeable
                );
                cellStateRepository.save(tick.getColumnType(), newCellState);
              }
            );
          clockmodes
            .get(action.getTimerType())
            .applyNewClockState(
              this.tick,
              tick,
              cellStateRepository
                .getColumn(tick.getColumnType())
                .getCellStates()
                .entrySet()
                .iterator()
                .next()
                .getValue()
                .getCurrentDirection()
            );
          return cellStateRepository
            .getColumn(tick.getColumnType())
            .getCellStates()
            .entrySet()
            .stream()
            .map(cell -> cell.getValue())
            .map(
              cellState -> {
                if (cellState.getCellStatePosition() == CellStatePosition.CHANGEABLE) {
                  return cellState.withTimerValue(
                    this.clockRepository.get(tick.getColumnType()).get(tick.getColumnType().getChronoField())
                  );
                }
                return cellState;
              }
            )
            .collect(Collectors.toList());
        }
      )
      .flatMap(cellStates -> cellStates.stream())
      .collect(Collectors.toList());
  }

  public Observable<CurrentClockState> tick(final TickAction action) {
    TickAction enrichedTickAction = List
      .of(action, action)
      .stream()
      .filter(ignored -> action.getTimerType() == TimerType.TICK)
      .reduce(
        (currentTickAction, nextTickAction) -> {
          if (
            shouldTickSpecificField
              .apply(ChronoField.SECOND_OF_MINUTE)
              .queryFrom(this.clockRepository.get(ColumnType.MAIN))
          ) {
            return currentTickAction.with(ColumnType.MINUTES, ChronoField.MINUTE_OF_HOUR, ChronoUnit.MINUTES);
          }
          if (
            shouldTickSpecificField
              .apply(ChronoField.SECOND_OF_MINUTE)
              .queryFrom(this.clockRepository.get(ColumnType.MAIN)) &&
            shouldTickSpecificField
              .apply(ChronoField.MINUTE_OF_HOUR)
              .queryFrom(this.clockRepository.get(ColumnType.MAIN))
          ) {
            return currentTickAction.with(ColumnType.HOURS, ChronoField.HOUR_OF_DAY, ChronoUnit.HOURS);
          }
          return currentTickAction;
        }
      )
      .orElse(action);

    List<CellState> cellStatesSoFar = produceCellsForTextChange(enrichedTickAction);
    return Observable.just(new CurrentClockState(cellStatesSoFar));
  }

  @Override
  public Map<ColumnType, Set<InitCellsMetadata>> initializeLabels() {
    LocalTime mainClock = this.clockRepository.get(ColumnType.MAIN);
    final var currentCellStatesSeconds = this.cellStateRepository.getColumn(ColumnType.SECONDS);
    final var currentCellStatesMinutes = this.cellStateRepository.getColumn(ColumnType.MINUTES);
    final var currentCellStatesHours = this.cellStateRepository.getColumn(ColumnType.HOURS);

    final var sortedCellSeconds = InitViewCellStateSorter.sortAndGetIterator(
      currentCellStatesSeconds,
      List.of(9, 10, 11, 12)
    );
    final var sortedCellsMinutes = InitViewCellStateSorter.sortAndGetIterator(
      currentCellStatesMinutes,
      List.of(5, 6, 7, 8)
    );
    final var sortedCellsHours = InitViewCellStateSorter.sortAndGetIterator(
      currentCellStatesHours,
      List.of(1, 2, 3, 4)
    );

    return IntStream
      .of(2, 1, 0, -1)
      .mapToObj(
        index -> {
          int second = this.tick.apply(index, ChronoUnit.SECONDS).apply(mainClock).getSecond();
          int minute = this.tick.apply(index, ChronoUnit.MINUTES).apply(mainClock).getMinute();
          int hour = this.tick.apply(index, ChronoUnit.HOURS).apply(mainClock).getHour();
          return Map.of(
            ColumnType.SECONDS,
            new InitCellsMetadata(sortedCellSeconds.next(), second),
            ColumnType.MINUTES,
            new InitCellsMetadata(sortedCellsMinutes.next(), minute),
            ColumnType.HOURS,
            new InitCellsMetadata(sortedCellsHours.next(), hour)
          );
        }
      )
      .flatMap(listOfMapOfColumnValues -> listOfMapOfColumnValues.entrySet().stream())
      .collect(
        groupingBy(
          mapOfColumnValues -> mapOfColumnValues.getKey(),
          mapping(mapOfColumnValues -> mapOfColumnValues.getValue(), Collectors.toUnmodifiableSet())
        )
      );
  }

  public static class InitCellsMetadata {
    private final int cellId;
    private final int cellValue;

    private InitCellsMetadata(int cellId, int cellValue) {
      this.cellId = cellId;
      this.cellValue = cellValue;
    }

    public int getCellId() {
      return cellId;
    }

    public int getCellValue() {
      return cellValue;
    }
  }
}
