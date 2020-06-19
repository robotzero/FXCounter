package com.robotzero.counter.domain;

public class Location {
  private final double fromY;
  private final double toY;

  public Location(final double fromY, final double toY) {
    this.fromY = fromY;
    this.toY = toY;
  }

  public double getFromY() {
    return fromY;
  }

  public double getToY() {
    return toY;
  }

  @Override
  public String toString() {
    return "Location{" + "fromY=" + fromY + ", toY=" + toY + '}';
  }
}
