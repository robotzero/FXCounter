package com.queen.counter.domain;

import java.time.LocalTime;

public class Clocks {

    private LocalTime mainClock          = LocalTime.of(0, 0, 0);
    private LocalTime scrollSecondsClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollMinutesClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollHoursClock   = LocalTime.of(0, 0, 0);

    private final int MIN = 59;
    private final int HR  = 23;

    //@TODO change to spring onCreate or similar.
    public void initializeClocks(final LocalTime mainClock) {
        this.mainClock = LocalTime.of(
                mainClock.getHour(),
                mainClock.getMinute(),
                mainClock.getSecond()
        );

        this.scrollHoursClock   = LocalTime.of(mainClock.getHour(), MIN, MIN);
        this.scrollMinutesClock = LocalTime.of(HR, mainClock.getMinute(), MIN);
        this.scrollSecondsClock = LocalTime.of(HR, MIN, mainClock.getSecond());
    }

    public LocalTime setScrollSecondsClock(int seconds) {
        this.scrollSecondsClock = scrollSecondsClock.plusSeconds(seconds);
        this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond());
        return this.scrollSecondsClock;
    }

    public LocalTime setScrollMinutesClock(int minutes) {
        this.scrollMinutesClock = scrollMinutesClock.plusMinutes(minutes);
        this.mainClock = mainClock.withMinute(scrollMinutesClock.getMinute());
        return this.scrollMinutesClock;
    }

    public LocalTime setScrollHoursClock(int hours) {
        this.scrollHoursClock = scrollHoursClock.plusHours(hours);
        this.mainClock = mainClock.withHour(scrollHoursClock.getHour());
        return this.scrollHoursClock;
    }

    public void clockTick(final String label, final double delta) {
        int normalizedDelta = (int) delta / (int) Math.abs(delta);

        if (label.contains("seconds")) {
            this.scrollSecondsClock = scrollSecondsClock.plusSeconds(normalizedDelta);
        }

        if (label.contains("minutes")) {
            this.scrollMinutesClock = scrollMinutesClock.plusMinutes(normalizedDelta);
        }

        if (label.contains("hours")) {
            this.scrollHoursClock = scrollHoursClock.plusHours(normalizedDelta);
        }

        this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
    }

    public int getTimeShift(final String label, final int timeOffset, final double delta) {
        int normalizedDelta = (int) delta / (int) Math.abs(delta);

        if (label.contains("seconds")) {
            return scrollSecondsClock.plusSeconds(timeOffset * normalizedDelta).getSecond();
        }

        if (label.contains("minutes")) {
            return scrollMinutesClock.plusMinutes(timeOffset * normalizedDelta).getMinute();
        }

        throw new IllegalArgumentException();
    }
}
