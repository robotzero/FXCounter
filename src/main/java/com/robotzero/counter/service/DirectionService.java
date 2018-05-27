package com.robotzero.counter.service;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import io.reactivex.Observable;

public class DirectionService {

    /* We have to track previous directions */
    private Direction currentDirectionSeconds;
    private Direction currentDirectionMinutes;
    private Direction currentDirectionHours;


    public Observable<Direction> calculateDirection(double translateY, double delta, ColumnType columnType) {
        Direction direction = null;
        if (delta < 0) {
            if (currentDirectionSeconds == null) {
                direction = Direction.UP;
                currentDirectionSeconds = direction;
                return Observable.just(direction);
            }

            if (currentDirectionSeconds.getDelta() == (int) (delta / Math.abs(delta))) {
                direction = Direction.UP;
            }

            if (translateY == -90) {

            }

            if (translateY == 270) {
                direction = Direction.SWITCHUP;
            }
        } else {
            if (currentDirectionSeconds == null) {
                direction = Direction.DOWN;
                currentDirectionSeconds = direction;
                return Observable.just(direction);
            }

            if (currentDirectionSeconds.getDelta() == (int) (delta / Math.abs(delta))) {
                direction = Direction.DOWN;
            }

            if (translateY == -90) {

            }

            if (translateY == 270) {

            }
        }

        if (direction == null) {
            throw new RuntimeException("Unsupported state");
        }
        currentDirectionSeconds = direction;
        return Observable.just(direction);
    }
}
