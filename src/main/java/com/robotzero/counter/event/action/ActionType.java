package com.robotzero.counter.event.action;

public enum ActionType {
    START("start"), PAUSE("pause"), RESET("reset"), SCROLL("scroll");

    private final String description;

    ActionType(String description) {
        this.description = description;
    }

    public String descripton() {
        return description;
    }
}
