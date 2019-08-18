package com.robotzero.counter.service;

import com.robotzero.counter.domain.Location;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class LocationService {
    private Function<Integer, Predicate<Integer>> isLessOrEqualThan = pivot -> {
        return candidate -> candidate <= pivot;
    };

    private Function<Integer, Predicate<Integer>> isGreaterOrEqualThan = pivot -> {
        return candidate -> candidate >= pivot;
    };

    public Location calculate(double delta, double translateY)
    {
        BiPredicate<Integer, Integer> blah = (a, b) -> {
            return a <=0 && b <=-90;
        };

        if (delta <= 0 && translateY <= -90) {
            return new Location(270, 180);
        }

        if (delta <= 0 && translateY > -90 ) {
            return new Location(translateY, translateY - 90);
        }

        if (delta > 0 && translateY == 270) {
            return new Location(-90, 0);
        }

        if (delta > 0 && translateY != 270) {
            return new Location(translateY, translateY + 90);
        }

        throw new RuntimeException("NAH");
    }
}
