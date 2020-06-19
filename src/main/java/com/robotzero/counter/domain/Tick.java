package com.robotzero.counter.domain;

import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class Tick {
  private final ColumnType columnType;
  private final ChronoUnit chronoUnit;
  private final ChronoField chronoField;

  public Tick(final ColumnType columnType, final ChronoUnit chronoUnit, final ChronoField chronoField) {
    this.columnType = columnType;
    this.chronoUnit = chronoUnit;
    this.chronoField = chronoField;
  }

  public ColumnType getColumnType() {
    return columnType;
  }

  public ChronoUnit getChronoUnit() {
    return chronoUnit;
  }

  public ChronoField getChronoField() {
    return chronoField;
  }

  public Optional<Boolean> shouldAdjustMainClock() {
    return Optional.of(this.columnType == ColumnType.SECONDS);
  }
}
