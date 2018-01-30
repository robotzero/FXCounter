package com.robotzero.counter.service;

import com.robotzero.counter.domain.Direction;

public class DirectionService {
    public Direction calculateDirection(double delta) {
        if (Math.abs(delta) < 0) {
            return Direction.DOWN;
        }

        if (Math.abs(delta) > 0) {
            return Direction.UP;
        }

        throw new UnsupportedOperationException("");
    }
}
