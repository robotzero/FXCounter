package com.robotzero.counter.domain;

public enum DirectionType {
    UP(-1), DOWN(1), SWITCHUP(-3), SWITCHDOWN(3), STARTUP(-2), STARTDOWN(2), VOID(0);

    private final int delta;

    DirectionType(int delta) {
        this.delta = delta;
    }

    public int getDelta() {
        return delta;
    }
}