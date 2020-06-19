package com.robotzero.counter.entity;

import java.time.Instant;
import java.time.LocalTime;

public class Clock {
  private String name;
  private LocalTime savedTimer;
  private Instant created;

  public LocalTime getSavedTimer() {
    return savedTimer;
  }

  public void setSavedTimer(final LocalTime savedTimer) {
    this.savedTimer = savedTimer;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Instant getCreated() {
    return created;
  }

  public void setCreated(final Instant created) {
    this.created = created;
  }
}
