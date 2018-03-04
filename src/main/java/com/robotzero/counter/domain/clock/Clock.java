package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.Direction;
import io.reactivex.Observable;

public interface Clock {
    public Observable<Integer> tick(Direction direction);
}
