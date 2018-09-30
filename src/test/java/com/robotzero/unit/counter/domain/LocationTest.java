package com.robotzero.unit.counter.domain;

import com.robotzero.counter.service.LocationService;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class LocationTest {

    private LocationService location = new LocationService();

    @DataProvider
    public static Object[][] fromYValues() {
        // currentCellSize, delta, translateY, expected result
        return new Object[][] {
                { new SimpleIntegerProperty(30), -1, -1, 120},
                { new SimpleIntegerProperty(30), 0, -1, 120},
                { new SimpleIntegerProperty(30), -1, 0, 120},
                { new SimpleIntegerProperty(30), 0, 0, 120},
                { new SimpleIntegerProperty(30), 0, 360, 360},
                { new SimpleIntegerProperty(30), 1, 120, 0},
                { new SimpleIntegerProperty(30), 1, 140, 140},
        };
    }

    @DataProvider
    public static Object[][] toYValues() {
        // currentCellSize, delta, translateY, expected result
        return new Object[][] {
                { new SimpleIntegerProperty(30), -1, -1, 90},
                { new SimpleIntegerProperty(30), 0, -1, 90},
                { new SimpleIntegerProperty(30), -1, 0, 90},
                { new SimpleIntegerProperty(30), 0, 0, 90},
                { new SimpleIntegerProperty(30), 0, 360, 330},
                { new SimpleIntegerProperty(30), 1, 120, 30},
                { new SimpleIntegerProperty(30), 1, 140, 170},
        };
    }

    @Test
    @UseDataProvider("fromYValues")
    public void calculateFromY(IntegerProperty currentCellSize, Integer delta, double translateY, double expectedResult) throws Exception {
        double newLocation = location.calculateFromY(currentCellSize, delta, translateY);
        Assert.assertEquals(expectedResult, newLocation, 0);
    }

    @Test
    @UseDataProvider("toYValues")
    public void calculateToY(IntegerProperty currentCellSize, Integer delta, double translateY, double expectedResult) throws Exception {
        double newLocation = location.calculateToY(currentCellSize, delta, translateY);
        Assert.assertEquals(expectedResult, newLocation, 0);
    }

}