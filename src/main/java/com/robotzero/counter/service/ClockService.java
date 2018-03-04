package com.robotzero.counter.service;

import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.clock.Clock;
import io.reactivex.Observable;

public class ClockService {
    private final Clock clock;

    public ClockService(Clock clock) {
        this.clock = clock;
    }

    public Observable tick(Direction direction) {
        return clock.tick(direction);
    }
}
