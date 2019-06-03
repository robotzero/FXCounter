package com.robotzero.counter.service;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Location;
import javafx.beans.property.IntegerProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LocationService {
    private Map<ColumnType, Map<Integer, Location>> currentLocation = new HashMap<>();
    private Map<ColumnType, Map<Integer, Location>> previousLocation;

    private Function<Integer, Predicate<Integer>> isLessOrEqualThan = pivot -> {
        return candidate -> candidate <= pivot;
    };

    private Function<Integer, Predicate<Integer>> isGreaterOrEqualThan = pivot -> {
        return candidate -> candidate >= pivot;
    };

    private Function<Function<Integer, Predicate<Integer>>, BiFunction<IntegerProperty, Double, Supplier<Integer>>> blah = function -> {
        if(function.apply(3).and(isLessOrEqualThan.apply(4)).test(1)) {
            return (cellsize, delta) -> {
                return () -> cellsize.multiply(4).get();
            };
        }
        return (delta, pivot) -> () -> 4;
    };

    public Location calculate(double delta, double translateY)
    {
        if (delta <= 0) {
            if (translateY <= -90) {
                return new Location(270, 180);
//                return currentCellSize.multiply(3).get();
            } else {
                return new Location(translateY, translateY - 90);
//                return translateY;
            }
        } else {
            if (translateY == 270) {
                return new Location(-90, 0);
//                return -90;
            } else {
                return new Location(translateY, translateY + 90);
//                return translateY;
            }
        }
    }

//    public double calculateToY(IntegerProperty currentCellSize, double delta, double translateY)
//    {
//        if (delta <= 0) {
//            if (translateY <= -90) {
//                return currentCellSize.multiply(3).get() - currentCellSize.get();
//            } else {
//                return translateY - currentCellSize.get();
//            }
//        } else {
//            if (translateY == currentCellSize.multiply(3).get()) {
//                return 0;
//            } else {
//                return translateY + currentCellSize.get();
//            }
//        }
//    }
}
