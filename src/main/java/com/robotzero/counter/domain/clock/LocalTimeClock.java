package com.robotzero.counter.domain.clock;

import static java.util.stream.Collectors.*;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.event.action.TickAction;
import com.robotzero.counter.service.DirectionService;
import com.robotzero.counter.service.LocationMemoizerKey;
import com.robotzero.counter.service.LocationService;
import io.reactivex.rxjava3.core.Observable;
import java.time.LocalTime;
import java.time.temporal.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    this.clockRepository.initialize(
        Optional
          .ofNullable(timerRepository.selectLatest())
          .orElseGet(
            () -> {
              com.robotzero.counter.entity.Clock savedTimer = new com.robotzero.counter.entity.Clock();
              savedTimer.setSavedTimer(LocalTime.of(22, 1, 10));
              return savedTimer;
            }
          )
          .getSavedTimer()
      );
  }

  private List<CellState> produceCellsForTextChange(final TickAction action) {
    return action
      .getTickData()
      .parallelStream()
      .map(
        tick -> {
          cellStateRepository
            .getColumn(tick.getColumnType())
            .getCells()
            .forEach(
              (key, cell) -> {
                Location newLocation = locationService.calculate(
                  new LocationMemoizerKey(
                    action.getDelta(),
                    cell.getCellState().getCurrentLocation().getToY(),
                    tick.getColumnType()
                  )
                );
                Direction newDirection = directionService.calculateDirection(
                  tick.getColumnType(),
                  cell.getCellState().getCurrentDirection(),
                  action.getDelta()
                );
                CellStatePosition isChangeable =
                  this.changeableStates.stream()
                    .filter(
                      state -> {
                        return state.supports(
                          newLocation.getFromY(),
                          cell.getCellState().getCurrentDirection()
                        );
                      }
                    )
                    .map(state -> CellStatePosition.CHANGEABLE)
                    .findAny()
                    .orElseGet(() -> CellStatePosition.NONCHANGABLE);
                final var newCellState = cell
                  .getCellState()
                  .createNew(
                    newLocation,
                    newDirection.getDirectionType(),
                    cell.getCellState().getCurrentDirection(),
                    isChangeable
                  );
                cell.setCellState(newCellState);
              }
            );

          //            cellStateRepository.save(tick.getColumnType(), updatedCellState);
          clockmodes
            .get(action.getTimerType())
            .applyNewClockState(
              this.tick,
              tick,
              cellStateRepository
                .getColumn(tick.getColumnType())
                .getCells()
                .get(0)
                .getCellState()
                .getCurrentDirection()
            );
          return cellStateRepository
            .getColumn(tick.getColumnType())
            .getCells()
            .entrySet()
            .stream()
            .filter(
              cells ->
                cells.getValue().getCellState().getCellStatePosition() ==
                CellStatePosition.CHANGEABLE
            )
            .map(cell -> cell.getValue().getCellState())
            .map(
              cellState -> {
                return cellState.withTimerValue(
                  this.clockRepository.get(cellState.getColumnType())
                    .get(cellState.getColumnType().getChronoField())
                );
              }
            )
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Nah"));
          //            return updatedCellState.stream().filter(cellState -> cellState.getCellStatePosition() == CellStatePosition.CHANGEABLE).map(cellState -> {
          //                return cellState.withTimerValue(this.clockRepository.get(cellState.getColumnType()).get(cellState.getColumnType().getChronoField()));
          //            }).findFirst().orElseThrow(() -> new RuntimeException("Nah"));
        }
      )
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
            return currentTickAction.with(
              ColumnType.MINUTES,
              ChronoField.MINUTE_OF_HOUR,
              ChronoUnit.MINUTES
            );
          }
          if (
            shouldTickSpecificField
              .apply(ChronoField.SECOND_OF_MINUTE)
              .queryFrom(this.clockRepository.get(ColumnType.MAIN)) &&
            shouldTickSpecificField
              .apply(ChronoField.MINUTE_OF_HOUR)
              .queryFrom(this.clockRepository.get(ColumnType.MAIN))
          ) {
            return currentTickAction.with(
              ColumnType.HOURS,
              ChronoField.HOUR_OF_DAY,
              ChronoUnit.HOURS
            );
          }
          return currentTickAction;
        }
      )
      .orElse(action);

    List<CellState> cellStatesSoFar = produceCellsForTextChange(
      enrichedTickAction
    );

    return Observable.just(new CurrentClockState(cellStatesSoFar));
  }

  @Override
  public Map<ColumnType, List<Integer>> initializeLabels() {
    LocalTime mainClock = this.clockRepository.get(ColumnType.MAIN);
    return IntStream
      .of(-1, 0, 1, 2)
      .mapToObj(
        index -> {
          int second =
            this.tick.apply(index, ChronoUnit.SECONDS)
              .apply(mainClock)
              .getSecond();
          int minute =
            this.tick.apply(index, ChronoUnit.MINUTES)
              .apply(mainClock)
              .getMinute();
          int hour =
            this.tick.apply(index, ChronoUnit.HOURS).apply(mainClock).getHour();
          return Map.of(
            ColumnType.SECONDS,
            second,
            ColumnType.MINUTES,
            minute,
            ColumnType.HOURS,
            hour
          );
        }
      )
      .flatMap(c -> c.entrySet().stream())
      .collect(
        groupingBy(
          map -> map.getKey(),
          mapping(
            map -> map.getValue(),
            collectingAndThen(
              toList(),
              listOfIntegers -> {
                listOfIntegers.sort(Collections.reverseOrder());
                return listOfIntegers;
              }
            )
          )
        )
      );
  }
}
