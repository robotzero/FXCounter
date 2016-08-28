package com.queen.counter.domain;

import org.reactfx.EventSource;
import org.reactfx.util.Tuple2;

import java.time.LocalTime;
import java.util.List;

public class Clocks {

    private final EventSource<Integer> eventSeconds;
    private final EventSource<Integer> eventMinutes;
    private final EventSource<Integer> eventHours;

    private final EventSource<Void> playMinutes;
    private final EventSource<Void> playHours;
    private final EventSource<Tuple2<Integer, ColumnType>> deltaStream;

    private LocalTime mainClock          = LocalTime.of(0, 0, 0);
    private LocalTime scrollSecondsClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollMinutesClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollHoursClock   = LocalTime.of(0, 0, 0);

    private final int MIN = 59;
    private final int HR  = 23;

    public Clocks(List<EventSource<Void>> playSources, EventSource<Tuple2<Integer, ColumnType>> deltaStream, EventSource<Integer> ...eventSources) {
        this.eventSeconds = eventSources[0];
        this.eventMinutes = eventSources[1];
        this.eventHours = eventSources[2];

        this.playMinutes = playSources.get(0);
        this.playHours = playSources.get(1);

        this.deltaStream = deltaStream;
        this.deltaStream.subscribe(currentTuple -> {
            int delta = currentTuple.get1();
            ColumnType type = currentTuple.get2();

            if (delta != 0) {
                int normalizedDelta = delta / Math.abs(delta);
                if (type.equals(ColumnType.SECONDS)) {
                    this.scrollSecondsClock = scrollSecondsClock.plusSeconds(normalizedDelta);
                    this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
                    this.eventSeconds.push(scrollSecondsClock.plusSeconds(normalizedDelta).getSecond());
                }

                if (type.equals(ColumnType.MINUTES)) {
                    this.scrollMinutesClock = scrollMinutesClock.plusMinutes(normalizedDelta);
                    this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
                    this.eventMinutes.push(scrollMinutesClock.plusMinutes(normalizedDelta).getMinute());
                }

                if (type.equals(ColumnType.HOURS)) {
                    this.scrollHoursClock = scrollHoursClock.plusHours(normalizedDelta);
                    this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
                    this.eventHours.push(scrollHoursClock.plusHours(normalizedDelta).getHour());
                }
            }

            if (this.scrollSecondsClock.minusSeconds(1).getSecond() == MIN) {
                this.playMinutes.push(null);
            }

            if (this.scrollMinutesClock.minusMinutes(1).getMinute() == HR) {
                this.playHours.push(null);
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

//    BiFunction<Integer, Void, Tuple2<Integer, Optional<String>>> countdown =
//            (s, i) -> s == 1
//                    ? t(3, Optional.of("COUNTDOWN REACHED"))
//                    : t(s-1, Optional.empty());

//    void clockTick(final ColumnType type, final double delta) {
//        if (delta != 0) {
//            int normalizedDelta = (int) delta / (int) Math.abs(delta);
//            if (type.equals(ColumnType.SECONDS)) {
//                this.scrollSecondsClock = scrollSecondsClock.plusSeconds(normalizedDelta);
//                this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
//                this.eventSeconds.push(scrollSecondsClock.plusSeconds(normalizedDelta).getSecond());
//            }
//
//            if (type.equals(ColumnType.MINUTES)) {
//                this.scrollMinutesClock = scrollMinutesClock.plusMinutes(normalizedDelta);
//                this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
//                this.eventMinutes.push(scrollMinutesClock.plusMinutes(normalizedDelta).getMinute());
//            }
//
//            if (type.equals(ColumnType.HOURS)) {
//                this.scrollHoursClock = scrollHoursClock.plusHours(normalizedDelta);
//                this.mainClock = mainClock.withSecond(scrollSecondsClock.getSecond()).withMinute(scrollMinutesClock.getMinute());
//                this.eventHours.push(scrollHoursClock.plusHours(normalizedDelta).getHour());
//            }
//        }
//
//        if (this.scrollSecondsClock.minusSeconds(1).getSecond() == MIN) {
//            this.playMinutes.push(null);
//        }
//
//        if (this.scrollMinutesClock.minusMinutes(1).getMinute() == HR) {
//            this.playHours.push(null);
//        }
//    }
}
