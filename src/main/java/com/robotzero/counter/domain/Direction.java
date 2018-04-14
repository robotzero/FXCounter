package com.robotzero.counter.domain;

public enum Direction {
    UP(1), DOWN(-1), SWITCHUP(-1), SWITCHDOWN(1), STARTUP(-1), STARTDOWN(-2);

    private final int delta;

    Direction(int delta) {
        this.delta = delta;
    }

    public int getDelta() {
        return delta;
    }
}