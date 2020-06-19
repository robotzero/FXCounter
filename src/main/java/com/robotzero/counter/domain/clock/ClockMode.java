package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.DirectionType;
import com.robotzero.counter.domain.Tick;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ClockMode {
  void applyNewClockState(
    BiFunction<Integer, ChronoUnit, Function<LocalTime, LocalTime>> tick,
    Tick tickData,
    DirectionType direction
  );
}
