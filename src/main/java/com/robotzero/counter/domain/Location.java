package com.robotzero.counter.domain;

import javafx.beans.property.IntegerProperty;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Location {
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

    public double calculateFromY(IntegerProperty currentCellSize, Integer delta, double translateY)
    {
        if (delta <= 0) {
            if (translateY <= -90) {
                return currentCellSize.multiply(3).get();
            } else {
                return translateY;
            }
        } else {
            if (translateY == currentCellSize.multiply(3).get()) {
                return -90;
            } else {
                return translateY;
            }
        }
    }

    public double calculateToY(IntegerProperty currentCellSize, Integer delta, double translateY)
    {
        if (delta <= 0) {
            if (translateY <= -90) {
                return currentCellSize.multiply(2).get();
            } else {
                return translateY - currentCellSize.get();
            }
        } else {
            if (translateY == currentCellSize.multiply(2).get()) {
                return currentCellSize.get();
            } else {
                return translateY + currentCellSize.get();
            }
        }
    }
}
