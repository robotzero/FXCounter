package com.robotzero.counter.event.action;

import com.robotzero.counter.domain.Direction;

public class TickAction implements Action {

    private final Direction direction;

    public TickAction(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}
