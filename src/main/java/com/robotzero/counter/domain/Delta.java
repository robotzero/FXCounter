package com.robotzero.counter.domain;

public enum Delta {
  UP(-1),
  DOWN(1);

  private double delta;

  Delta(double delta) {
    this.delta = delta;
  }

  public double getDelta() {
    return delta;
  }
}
