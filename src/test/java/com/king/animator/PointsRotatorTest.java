package com.king.animator;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import javafx.geometry.Point2D;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@RunWith(DataProviderRunner.class)
public class PointsRotatorTest
{
    @DataProvider
    public static Object[][] hashMapAndTickProvider() {

        int tick1 = 1;
        int tick2 = 2;
        int tick3 = 3;
        int tick4 = 4;
        int tick5 = 5;

        Map hash1A = new HashMap<>();
        Map hash2A = new HashMap<>();
        Map hash3A = new HashMap<>();
        Map hash4A = new HashMap<>();

        Map hash1B = new HashMap<>();
        Map hash2B = new HashMap<>();
        Map hash3B = new HashMap<>();
        Map hash4B = new HashMap<>();

        Map hash1C = new HashMap<>();
        Map hash2C = new HashMap<>();
        Map hash3C = new HashMap<>();
        Map hash4C = new HashMap<>();

        Map hash1D = new HashMap<>();
        Map hash2D = new HashMap<>();
        Map hash3D = new HashMap<>();
        Map hash4D = new HashMap<>();

        hash1A.put("from", new Point2D(0, 240));
        hash1A.put("to", new Point2D(0, 180));

        hash2A.put("from", new Point2D(0, 60));
        hash2A.put("to", new Point2D(0, 0));

        hash3A.put("from", new Point2D(0, 120));
        hash3A.put("to", new Point2D(0, 60));

        hash4A.put("from", new Point2D(0, 180));
        hash4A.put("to", new Point2D(0, 120));

        hash1B.put("from", new Point2D(0, 180));
        hash1B.put("to", new Point2D(0, 120));

        hash2B.put("from", new Point2D(0, 240));
        hash2B.put("to", new Point2D(0, 180));

        hash3B.put("from", new Point2D(0, 60));
        hash3B.put("to", new Point2D(0, 0));

        hash4B.put("from", new Point2D(0, 120));
        hash4B.put("to", new Point2D(0, 60));

        hash1C.put("from", new Point2D(0, 120));
        hash1C.put("to", new Point2D(0, 60));

        hash2C.put("from", new Point2D(0, 180));
        hash2C.put("to", new Point2D(0, 120));

        hash3C.put("from", new Point2D(0, 240));
        hash3C.put("to", new Point2D(0, 180));

        hash4C.put("from", new Point2D(0, 60));
        hash4C.put("to", new Point2D(0, 0));

        hash1D.put("from", new Point2D(0, 60));
        hash1D.put("to", new Point2D(0, 0));

        hash2D.put("from", new Point2D(0, 120));
        hash2D.put("to", new Point2D(0, 60));

        hash3D.put("from", new Point2D(0, 180));
        hash3D.put("to", new Point2D(0, 120));

        hash4D.put("from", new Point2D(0, 240));
        hash4D.put("to", new Point2D(0, 180));

        return new Object[][] {
            { tick1, hash1A, hash2A, hash3A, hash4A },
            { tick2, hash1B, hash2B, hash3B, hash4B },
            { tick3, hash1C, hash2C, hash3C, hash4C },
            { tick4, hash1D, hash2D, hash3D, hash4D },
            { tick5, hash1A, hash2A, hash3A, hash4A },
        };
    }

    @Test
    @UseDataProvider("hashMapAndTickProvider")
    public void testFullRotation(int tick, Map hash1, Map hash2, Map hash3, Map hash4) {


        List<Point2D> from = new LinkedList<>();
        from.add(new Point2D(0, 60));
        from.add(new Point2D(0, 120));
        from.add(new Point2D(0, 180));
        from.add(new Point2D(0, 240));

//        List<Map<String, Point2D>> result = rotator.rotate(from);
//
//        assertThat(result, contains(hash1, hash2, hash3, hash4));
    }
}
