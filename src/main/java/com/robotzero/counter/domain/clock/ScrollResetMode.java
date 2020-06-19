package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.DirectionType;
import com.robotzero.counter.domain.Tick;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ScrollResetMode implements ClockMode {
  private final ClockRepository clockRepository;

  public ScrollResetMode(ClockRepository clockRepository) {
    this.clockRepository = clockRepository;
  }

  @Override
  public void applyNewClockState(
    BiFunction<Integer, ChronoUnit, Function<LocalTime, LocalTime>> tick,
    Tick tickData,
    DirectionType direction
  ) {
    LocalTime currentMain = this.clockRepository.get(ColumnType.MAIN);
    LocalTime afterTickMain = tick.apply(direction.getNormalizedDelta(), tickData.getChronoUnit()).apply(currentMain);
    this.clockRepository.save(
        tickData.getColumnType(),
        tick
          .apply(direction.getDelta(), tickData.getChronoUnit())
          .apply(this.clockRepository.get(tickData.getColumnType()))
      );
    this.clockRepository.save(
        ColumnType.MAIN,
        LocalTime.from(currentMain).with(tickData.getChronoField(), afterTickMain.get(tickData.getChronoField()))
      );
  }
}
