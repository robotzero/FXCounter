package com.robotzero.counter.event;

public enum ButtonState {
    START("Start"), STOP("Stop"), PAUSED("Paused"), RESET("Reset");

    private final String description;

    ButtonState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
