package com.robotzero.counter.service;

import com.robotzero.counter.domain.ColumnType;
import java.util.Objects;

public class LocationMemoizerKey {
  private final double delta;
  private final double translateY;
  private final ColumnType columnType;

  public LocationMemoizerKey(final double delta, final double translateY, final ColumnType columnType) {
    this.delta = delta;
    this.translateY = translateY;
    this.columnType = columnType;
  }

  public double getDelta() {
    return delta;
  }

  public double getTranslateY() {
    return translateY;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LocationMemoizerKey that = (LocationMemoizerKey) o;
    return (
      Double.compare(that.delta, delta) == 0 &&
      Double.compare(that.translateY, translateY) == 0 &&
      columnType == that.columnType
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(delta, translateY, columnType);
  }
}
