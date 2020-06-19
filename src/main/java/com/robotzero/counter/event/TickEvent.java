package com.robotzero.counter.event;

public class TickEvent implements MainViewEvent {
  private final Long elapsedTime;

  public TickEvent(Long elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

  public Long getElapsedTime() {
    return elapsedTime;
  }
}
