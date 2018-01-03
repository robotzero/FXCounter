package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.entity.Clock;
import io.reactivex.subjects.Subject;
import org.reactfx.EventSource;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Clocks {

    private final ClockRepository clockRepository;
    private final EventSource<Integer> eventSeconds;
    private final EventSource<Integer> eventMinutes;
    private final EventSource<Integer> eventHours;

    private final EventSource<Void> playMinutes;
    private final EventSource<Void> playHours;
    private final EventSource<Void> stopCountdown;

    private LocalTime mainClock          = LocalTime.of(0, 0, 0);
    private LocalTime scrollSecondsClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollMinutesClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollHoursClock   = LocalTime.of(0, 0, 0);

    private Predicate<Integer> isDeltaLessThan = delta -> delta < 0;
    private BiFunction<Predicate<Integer>, Integer, BiFunction<LocalTime, Integer, LocalTime>> tick = (predicate, currentDelta) -> {
        if (predicate.test(currentDelta)) {
            return LocalTime::plusSeconds;
        }
        return LocalTime::minusSeconds;
    };

    private Function<Integer, LocalTime> calculateLabel = delta -> {
        LocalTime tempClock = LocalTime.of(this.mainClock.getSecond(), this.mainClock.getMinute(), this.mainClock.getHour());
        return delta < 0 ? tempClock.plusSeconds(2) : tempClock.minusSeconds(2);
    };

    private Function<Integer, Predicate<Integer>> isGreaterThan = pivot -> {
        return candidate -> candidate > pivot;
    };

    private Function<Integer, Integer> normalizeDelta = delta -> delta / Math.abs(delta);

    private final int MIN = 59;
    private final int HR  = 23;

    public Clocks(ClockRepository clockRepository, List<EventSource<Void>> playSources, List<Subject<Direction>> deltaStreams, EventSource<Integer> ...eventSources) {
//        System.out.println(isGreaterThan.apply(30).test(10));
        this.eventSeconds = eventSources[0];
        this.eventMinutes = eventSources[1];
        this.eventHours = eventSources[2];

        this.playMinutes = playSources.get(0);
        this.playHours = playSources.get(1);
        this.stopCountdown = playSources.get(2);

        this.clockRepository = clockRepository;

        deltaStreams.get(0).subscribe(currentDelta -> {
//            if (currentDelta.getDelta() != 0) {
                this.scrollSecondsClock = tick.apply(isDeltaLessThan, currentDelta.getDelta()).apply(this.scrollSecondsClock, 1);
//                this.scrollSecondsClock = pS.apply(this.scrollSecondsClock, normalizeDelta.apply(currentDelta.getDelta()));
//                this.mainClock = clockTick.apply(mainClock, currentDelta);
//                this.eventSeconds.push(pS.apply(this.scrollSecondsClock, normalizeDelta.apply(currentDelta.getDelta())).getSecond());
                this.eventSeconds.push(this.scrollSecondsClock.getSecond());

                // Count down has ended.
                if (this.mainClock == LocalTime.of(0, 0, 0)) {
                    this.stopCountdown.push(null);
                }
                if (this.scrollSecondsClock.getSecond() == MIN) {
                    this.playMinutes.push(null);
                }
//            }
        });

//        deltaStreams.get(1).subscribe(currentDelta -> {
//            if (currentDelta != 0) {
//                this.scrollMinutesClock = pM.apply(this.scrollMinutesClock, normalizeDelta.apply(currentDelta));
//                this.mainClock = clockTick.apply(mainClock);
//                this.eventMinutes.push(scrollMinutesClock.plusMinutes(normalizeDelta.apply(currentDelta)).getMinute());
//
//                if (this.scrollMinutesClock.getMinute() == HR) {
//                    this.playHours.push(null);
//                }
//            }
//        });
//
//        deltaStreams.get(2).subscribe(currentDelta -> {
//            if (currentDelta != 0) {
//                this.scrollHoursClock = pH.apply(this.scrollHoursClock, normalizeDelta.apply(currentDelta));
//                this.mainClock = clockTick.apply(mainClock);
//                this.eventHours.push(scrollHoursClock.plusHours(normalizeDelta.apply(currentDelta)).getHour());
//            }
//        });
    }

    @PostConstruct
    public void initialize() {
        this.mainClock = Optional.ofNullable(clockRepository.selectLatest()).orElseGet(() -> {
            Clock savedTimer = new Clock();
            savedTimer.setSavedTimer(LocalTime.of(22, 10, 0));
            return savedTimer;
        }).getSavedTimer();
    }

    public void initializeClocks(final LocalTime mainClock) {
        this.scrollHoursClock   = LocalTime.of(mainClock.getHour(), MIN, MIN);
        this.scrollMinutesClock = LocalTime.of(HR, mainClock.getMinute(), MIN);
        this.scrollSecondsClock = LocalTime.of(HR, MIN, mainClock.getSecond());

        eventSeconds.push(this.scrollSecondsClock.plusSeconds(2).getSecond());
        eventMinutes.push(this.scrollMinutesClock.plusMinutes(2).getMinute());
        eventHours.push(this.scrollHoursClock.plusHours(2).getHour());
    }

    public LocalTime mainClockTick(Direction direction) {
        return this.tick.apply(isDeltaLessThan, direction.getDelta()).apply(this.mainClock, 1);
    }

    public LocalTime getMainClock() {
        return this.mainClock;
    }
}
