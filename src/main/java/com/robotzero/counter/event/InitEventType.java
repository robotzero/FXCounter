package com.robotzero.counter.event;

public enum InitEventType {
  SHOW("StageShow"),
  RESET("Reset");

  private final String name;

  InitEventType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
