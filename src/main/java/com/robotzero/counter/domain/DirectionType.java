package com.robotzero.counter.domain;

public enum DirectionType {
  UP(-1),
  DOWN(1),
  SWITCHUP(-3),
  SWITCHDOWN(3),
  STARTUP(-2),
  STARTDOWN(2),
  VOIDUP(-1),
  VOIDDOWN(1),
  VOID(0);

  private final int delta;

  DirectionType(final int delta) {
    this.delta = delta;
  }

  public int getDelta() {
    return delta;
  }

  public int getNormalizedDelta() {
    return this.delta / Math.abs(this.delta);
  }
}
