package com.robotzero.counter.event;

public enum ButtonType {
    START("Start"), PAUSE("Pause"), RESET("Reset");

    private final String description;

    ButtonType(String description) {
        this.description = description;
    }

    public String descripton() {
        return description;
    }
}
