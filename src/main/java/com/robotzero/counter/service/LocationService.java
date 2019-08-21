package com.robotzero.counter.service;

import com.robotzero.counter.domain.Location;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class LocationService {
    private Map<LocationMemoizerKey, Location> memo;
    {
        memo = new ConcurrentHashMap<>();
    }
    private Function<Integer, Predicate<Integer>> isLessOrEqualThan = pivot -> {
        return candidate -> candidate <= pivot;
    };

    private Function<Integer, Predicate<Integer>> isGreaterOrEqualThan = pivot -> {
        return candidate -> candidate >= pivot;
    };

    public Location calculate(LocationMemoizerKey locationMemoizerKey)
    {
        BiPredicate<Integer, Integer> blah = (a, b) -> {
            return a <=0 && b <=-90;
        };

        double delta = locationMemoizerKey.getDelta();
        double translateY = locationMemoizerKey.getTranslateY();

        return memo.computeIfAbsent(locationMemoizerKey, (locationMemoizerKey1 -> {
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
        }));
    }
}
