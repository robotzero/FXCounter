package com.queen.counter.domain;

import javafx.beans.property.IntegerProperty;

public class Location {

    double calculateFromY(IntegerProperty currentCellSize, Integer delta, double translateY)
    {
        if (delta <= 0) {
            if (translateY <= 0) {
                return currentCellSize.multiply(4).get();
            } else {
                return translateY;
            }
        } else {
            if (translateY == currentCellSize.multiply(4).get()) {
                return 0;
            } else {
                return translateY;
            }
        }
    }

    double calculateToY(IntegerProperty currentCellSize, Integer delta, double translateY)
    {
        if (delta <= 0) {
            if (translateY <= 0) {
                return currentCellSize.multiply(3).get();
            } else {
                return translateY - currentCellSize.get();
            }
        } else {
            if (translateY == currentCellSize.multiply(4).get()) {
                return currentCellSize.get();
            } else {
                return translateY + currentCellSize.get();
            }
        }
    }
}
