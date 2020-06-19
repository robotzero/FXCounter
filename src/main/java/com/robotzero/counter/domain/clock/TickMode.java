package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.DirectionType;
import com.robotzero.counter.domain.Tick;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TickMode implements ClockMode {
  private ClockRepository clockRepository;

  public TickMode(ClockRepository clockRepository) {
    this.clockRepository = clockRepository;
  }

  public void applyNewClockState(
    BiFunction<Integer, ChronoUnit, Function<LocalTime, LocalTime>> tick,
    Tick tickData,
    DirectionType direction
  ) {
    tickData
      .shouldAdjustMainClock()
      .filter(b -> b)
      .ifPresent(
        ignored -> {
          clockRepository.save(
            ColumnType.MAIN,
            tick
              .apply(direction.getNormalizedDelta(), ChronoUnit.SECONDS)
              .apply(clockRepository.get(ColumnType.MAIN))
          );
        }
      );
    clockRepository.save(
      tickData.getColumnType(),
      tick
        .apply(direction.getDelta(), tickData.getChronoUnit())
        .apply(clockRepository.get(tickData.getColumnType()))
    );
  }
}
