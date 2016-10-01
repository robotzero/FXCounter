package com.queen.counter.domain;

import org.reactfx.EventSource;

import java.time.LocalTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Clocks {

    private final EventSource<Integer> eventSeconds;
    private final EventSource<Integer> eventMinutes;
    private final EventSource<Integer> eventHours;

    private final EventSource<Void> playMinutes;
    private final EventSource<Void> playHours;

    private LocalTime mainClock          = LocalTime.of(0, 0, 0);
    private LocalTime scrollSecondsClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollMinutesClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollHoursClock   = LocalTime.of(0, 0, 0);

    private BiFunction<LocalTime, Integer, LocalTime> pS = LocalTime::plusSeconds;
    private BiFunction<LocalTime, Integer, LocalTime> pM = LocalTime::plusMinutes;
    private BiFunction<LocalTime, Integer, LocalTime> pH = LocalTime::plusMinutes;

    private Function<LocalTime, LocalTime> mainClockF = (c) -> c.withSecond(scrollSecondsClock.getSecond())
                                                                .withMinute(scrollMinutesClock.getMinute())
                                                                .withHour(scrollHoursClock.getHour());

    private Function<Integer, Integer> normalizeDelta = delta -> delta / Math.abs(delta);

    private final int MIN = 59;
    private final int HR  = 23;

    public Clocks(List<EventSource<Void>> playSources, List<EventSource<Integer>> deltaStreams, EventSource<Integer> ...eventSources) {
        this.eventSeconds = eventSources[0];
        this.eventMinutes = eventSources[1];
        this.eventHours = eventSources[2];

        this.playMinutes = playSources.get(0);
        this.playHours = playSources.get(1);

        deltaStreams.get(0).subscribe(currentDelta -> {
            if (currentDelta != 0) {
                this.scrollSecondsClock = pS.apply(this.scrollSecondsClock, normalizeDelta.apply(currentDelta));
                this.mainClock = mainClockF.apply(mainClock);
                this.eventSeconds.push(pS.apply(this.scrollSecondsClock, normalizeDelta.apply(currentDelta)).getSecond());
                if (this.scrollSecondsClock.getSecond() == MIN) {
                    this.playMinutes.push(null);
                }
//                if (this.scrollSecondsClock.minusSeconds(1).getSecond() == MIN) {
//                    this.playMinutes.push(null);
//                }
            }
        });

        deltaStreams.get(1).subscribe(currentDelta -> {
            if (currentDelta != 0) {
                this.scrollMinutesClock = pM.apply(this.scrollMinutesClock, normalizeDelta.apply(currentDelta));
                this.mainClock = mainClockF.apply(mainClock);
                this.eventMinutes.push(scrollMinutesClock.plusMinutes(normalizeDelta.apply(currentDelta)).getMinute());

                if (this.scrollMinutesClock.getMinute() == HR) {
                    this.playHours.push(null);
                }
            }
        });

        deltaStreams.get(2).subscribe(currentDelta -> {
            if (currentDelta != 0) {
                this.scrollHoursClock = pH.apply(this.scrollHoursClock, normalizeDelta.apply(currentDelta));
                this.mainClock = mainClockF.apply(mainClock);
                this.eventHours.push(scrollHoursClock.plusHours(normalizeDelta.apply(currentDelta)).getHour());
            }
        });
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

        eventSeconds.push(this.scrollSecondsClock.plusSeconds(2).getSecond());
        eventMinutes.push(this.scrollMinutesClock.plusMinutes(2).getMinute());
        eventHours.push(this.scrollHoursClock.plusHours(2).getHour());
    }

    public LocalTime getMainClock() {
        return this.mainClock;
    }
}
