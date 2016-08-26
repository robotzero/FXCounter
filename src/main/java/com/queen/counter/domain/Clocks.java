package com.queen.counter.domain;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.reactfx.EventSource;
import org.reactfx.util.Tuple2;

import java.time.LocalTime;

public class Clocks {

    private final EventSource event;
    private LocalTime mainClock          = LocalTime.of(0, 0, 0);
    private LocalTime scrollSecondsClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollMinutesClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollHoursClock   = LocalTime.of(0, 0, 0);
    private EventSource<Integer> timeShiftSecondsStream = new EventSource<>();
    private IntegerProperty timeShiftSeconds = new SimpleIntegerProperty(0);
    private IntegerProperty timeShiftMinutes = new SimpleIntegerProperty(0);
    private IntegerProperty timeShiftHours = new SimpleIntegerProperty(0);

    private final int MIN = 59;
    private final int HR  = 23;

    public Clocks(EventSource<Integer> event) {
        this.event = event;
    }
    public void initializeClocks(final LocalTime mainClock) {
        this.mainClock = LocalTime.of(
                mainClock.getHour(),
                mainClock.getMinute(),
                mainClock.getSecond()
        );

        this.scrollHoursClock   = LocalTime.of(mainClock.getHour(), MIN, MIN);
        this.scrollMinutesClock = LocalTime.of(HR, mainClock.getMinute(), MIN);
        this.scrollSecondsClock = LocalTime.of(HR, MIN, mainClock.getSecond());

        event.push(this.scrollSecondsClock.plusSeconds(2).getSecond());
        timeShiftSecondsStream.push(this.scrollSecondsClock.plusSeconds(2).getSecond());
        timeShiftSeconds.set(this.scrollSecondsClock.plusSeconds(2).getSecond());
        timeShiftMinutes.set(this.scrollMinutesClock.plusMinutes(2).getMinute());
        timeShiftHours.set(this.scrollHoursClock.plusHours(2).getHour());
    }

    public LocalTime getMainClock() {
        return this.mainClock;
    }

    public LocalTime getScrollSecondsClock() {
        return this.scrollSecondsClock;
    }

    public void clockTick(final ColumnType type, final double delta) {
        if (delta != 0) {
            int normalizedDelta = (int) delta / (int) Math.abs(delta);
            if (type.equals(ColumnType.SECONDS)) {
                this.scrollSecondsClock = scrollSecondsClock.plusSeconds(normalizedDelta);
                this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
                this.event.push(scrollSecondsClock.plusSeconds(normalizedDelta).getSecond());
            }

            if (type.equals(ColumnType.MINUTES)) {
                this.scrollMinutesClock = scrollMinutesClock.plusMinutes(normalizedDelta);
                this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
                this.event.push(scrollMinutesClock.plusMinutes(normalizedDelta).getMinute());
            }

            if (type.equals(ColumnType.HOURS)) {
                this.scrollHoursClock = scrollHoursClock.plusHours(normalizedDelta);
                this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
                this.event.push(scrollHoursClock.plusHours(normalizedDelta).getHour());
            }
        }
    }
}
