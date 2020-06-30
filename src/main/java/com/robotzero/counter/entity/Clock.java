package com.robotzero.counter.entity;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Clock {
  private String name;
  private LocalTime savedTimer;
  private Instant created;

  public Clock(String name, LocalTime localTime, Instant created) {
    this.name = name;
    this.savedTimer = localTime;
    this.created = created;
  }

  public LocalTime getSavedTimer() {
    return savedTimer;
  }

  public String getName() {
    return name;
  }

  public static Clock with(String name, String localTimeString, String createdString) {
    final List<Integer> timerValues = Arrays
      .stream(localTimeString.split(":"))
      .map(stringTimerValue -> Integer.parseInt(stringTimerValue))
      .collect(Collectors.toList());
    final var iterator = timerValues.iterator();
    final var localTime = LocalTime.of(
      iterator.hasNext() ? iterator.next() : 0,
      iterator.hasNext() ? iterator.next() : 0,
      iterator.hasNext() ? iterator.next() : 0
    );
    final var created = Instant.parse(createdString);
    return new Clock(name, localTime, created);
  }
}
