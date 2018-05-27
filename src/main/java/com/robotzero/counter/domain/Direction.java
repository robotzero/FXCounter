package com.robotzero.counter.domain;

public enum Direction {
    UP(-1), DOWN(1), SWITCHUP(0), SWITCHDOWN(1), STARTUP(2), STARTDOWN(-2);

    private final int delta;

    Direction(int delta) {
        this.delta = delta;
    }

    public int getDelta() {
        return delta;
    }
}