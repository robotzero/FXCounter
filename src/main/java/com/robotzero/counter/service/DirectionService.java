package com.robotzero.counter.service;

import com.robotzero.counter.domain.Direction;
import io.reactivex.Observable;

public class DirectionService {
    public Observable<Direction> calculateDirection(double delta) {
        System.out.println("DELTA" + delta);
        if (delta < 0) {
            return Observable.just(Direction.DOWN);
        }

        return Observable.just(Direction.UP);
    }
}
