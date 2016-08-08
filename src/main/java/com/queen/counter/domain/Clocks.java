package com.queen.counter.domain;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.reactfx.EventSource;
import java.time.LocalTime;

public class Clocks {

    private LocalTime mainClock          = LocalTime.of(0, 0, 0);
    private LocalTime scrollSecondsClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollMinutesClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollHoursClock   = LocalTime.of(0, 0, 0);
    private EventSource<Boolean> eventSource;
    private IntegerProperty timeShiftSeconds = new SimpleIntegerProperty(0);
    private IntegerProperty timeShiftMinutes = new SimpleIntegerProperty(0);
    private IntegerProperty timeShiftHours = new SimpleIntegerProperty(0);

    private final int MIN = 59;
    private final int HR  = 23;

    public Clocks(EventSource<Boolean> eventSource) {
        this.eventSource = eventSource;
    }

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

        timeShiftSeconds.set(this.scrollSecondsClock.plusSeconds(2).getSecond());
        timeShiftMinutes.set(this.scrollMinutesClock.plusMinutes(2).getMinute());
        timeShiftHours.set(this.scrollHoursClock.plusHours(2).getHour());
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

    public LocalTime getMainClock() {
        return this.mainClock;
    }

    public LocalTime getScrollSecondsClock() {
        return this.scrollSecondsClock;
    }

    public void clockTick(final ColumnType type, final double delta, int timeOffset) {
        int normalizedDelta = (int) delta / (int) Math.abs(delta);

        if (type.equals(ColumnType.SECONDS)) {
            this.scrollSecondsClock = scrollSecondsClock.plusSeconds(normalizedDelta);
            this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
            timeShiftSeconds.set(scrollSecondsClock.plusSeconds(timeOffset * normalizedDelta).getSecond());
        }

        if (type.equals(ColumnType.MINUTES)) {
            this.scrollMinutesClock = scrollMinutesClock.plusMinutes(normalizedDelta);
            this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
            timeShiftMinutes.set(scrollMinutesClock.plusMinutes(timeOffset * normalizedDelta).getMinute());
        }

        if (type.equals(ColumnType.HOURS)) {
            this.scrollHoursClock = scrollHoursClock.plusHours(normalizedDelta);
            this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
            timeShiftHours.set(scrollHoursClock.plusHours(timeOffset * normalizedDelta).getHour());
        }
    }

    public IntegerProperty getTimeShift(ColumnType type) {
        if (type.equals(ColumnType.SECONDS)) {
            return this.timeShiftSeconds;
        }
        if (type.equals(ColumnType.MINUTES)) {
            return this.timeShiftMinutes;
        }
        return this.timeShiftHours;
    }
}
