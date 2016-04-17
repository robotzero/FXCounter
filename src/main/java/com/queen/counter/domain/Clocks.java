package com.queen.counter.domain;

import java.time.LocalTime;
import java.util.function.Function;

public class Clocks {

    private LocalTime mainClock          = LocalTime.of(0, 0, 0);
    private LocalTime scrollSecondsClock = LocalTime.of(0, 0, 0);;
    private LocalTime scrollMinutesClock = LocalTime.of(0, 0, 0);;
    private LocalTime scrollHoursClock   = LocalTime.of(0, 0, 0);;

    public void clockTick(final LocalTime currentClock, Function<LocalTime, LocalTime> tick) {
        this.mainClock = tick.apply(currentClock);
    }

    public void initializeClocks(final LocalTime mainClock) {
        this.mainClock = LocalTime.of(
                mainClock.getHour(),
                mainClock.getMinute(),
                mainClock.getSecond()
        );

        this.scrollHoursClock   = LocalTime.of(mainClock.getHour(), 59, 59);
        this.scrollMinutesClock = LocalTime.of(24, mainClock.getMinute(), 59);
        this.scrollSecondsClock = LocalTime.of(24, 59, mainClock.getSecond());
    }
}
