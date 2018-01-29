package com.robotzero.counter.event.action;

public enum ActionType {
    START("Start"), PAUSE("Pause"), RESET("Reset"), SCROLL("Scroll");

    private final String description;

    ActionType(String description) {
        this.description = description;
    }

    public String descripton() {
        return description;
    }
}
