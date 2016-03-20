package com.king.counter;

import org.junit.Test;

import static org.junit.Assert.*;

public class CounterTest {

    @Test
    public void testStop() throws Exception {

        Counter counter = new Counter();

        counter.stop();
    }
}