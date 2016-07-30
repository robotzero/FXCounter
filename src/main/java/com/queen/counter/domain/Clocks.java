package com.queen.counter.domain;

import javafx.event.EventType;
import org.reactfx.EventSink;
import org.reactfx.EventSource;

import java.time.LocalTime;

import static java.awt.event.MouseEvent.MOUSE_CLICKED;

public class Clocks {

    private LocalTime mainClock          = LocalTime.of(0, 0, 0);
    private LocalTime scrollSecondsClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollMinutesClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollHoursClock   = LocalTime.of(0, 0, 0);
    private EventSource<Boolean> eventSource;

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

    public int clockTick(final String label, final double delta, int timeOffset) {
        int normalizedDelta = (int) delta / (int) Math.abs(delta);

        if (label.equals("seconds")) {
            this.scrollSecondsClock = scrollSecondsClock.plusSeconds(normalizedDelta);
            this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
            return scrollSecondsClock.plusSeconds(timeOffset * normalizedDelta).getSecond();
        }

        if (label.equals("minutes")) {
            this.scrollMinutesClock = scrollMinutesClock.plusMinutes(normalizedDelta);
            this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
            return scrollMinutesClock.plusMinutes(timeOffset * normalizedDelta).getMinute();
        }

        if (label.equals("hours")) {
            this.scrollHoursClock = scrollHoursClock.plusHours(normalizedDelta);
            this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
            return scrollHoursClock.plusHours(timeOffset * normalizedDelta).getHour();
        }

        return 0;
    }
}
