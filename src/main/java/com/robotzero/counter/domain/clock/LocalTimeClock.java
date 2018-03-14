package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;
import io.reactivex.Observable;
import io.reactivex.subjects.Subject;
import org.reactfx.EventSource;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class LocalTimeClock implements Clock {

    private final TimerRepository timerRepository;
    private final Subject<Integer> eventSeconds;
    private final Subject<Integer> eventMinutes;
    private final Subject<Integer> eventHours;

    private final EventSource<Void> playMinutes;
    private final EventSource<Void> playHours;
    private final EventSource<Void> stopCountdown;

    private LocalTime mainClock          = LocalTime.of(0, 0, 0);
    private LocalTime scrollSecondsClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollMinutesClock = LocalTime.of(0, 0, 0);
    private LocalTime scrollHoursClock   = LocalTime.of(0, 0, 0);

    private Predicate<Integer> isDeltaGreaterThan = delta -> delta > 0;
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

    private final long MIN = ChronoUnit.MINUTES.getDuration().toMinutes();
    private final long HR  = ChronoUnit.HOURS.getDuration().toHours();

    public LocalTimeClock(TimerRepository timerRepository, List<EventSource<Void>> playSources, List<Subject<Direction>> deltaStreams, Subject<Integer> ...eventSources) {
        this.eventSeconds = eventSources[0];
        this.eventMinutes = eventSources[1];
        this.eventHours = eventSources[2];

        this.playMinutes = playSources.get(0);
        this.playHours = playSources.get(1);
        this.stopCountdown = playSources.get(2);

        this.timerRepository = timerRepository;

        deltaStreams.get(0).subscribe(currentDelta -> {
            this.scrollSecondsClock = tick.apply(isDeltaGreaterThan, currentDelta.getDelta()).apply(this.scrollSecondsClock, 1);
            this.mainClock = tick.apply(isDeltaGreaterThan, currentDelta.getDelta()).apply(this.mainClock, 1);
//                this.scrollSecondsClock = pS.apply(this.scrollSecondsClock, normalizeDelta.apply(currentDelta.getDelta()));
//                this.mainClock = clockTick.apply(mainClock, currentDelta);
//                this.eventSeconds.push(pS.apply(this.scrollSecondsClock, normalizeDelta.apply(currentDelta.getDelta())).getSecond());
            this.eventSeconds.onNext(this.scrollSecondsClock.getSecond());

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
        this.mainClock = Optional.ofNullable(timerRepository.selectLatest()).orElseGet(() -> {
            com.robotzero.counter.entity.Clock savedTimer = new com.robotzero.counter.entity.Clock();
            savedTimer.setSavedTimer(LocalTime.of(22, 10, 0));
            return savedTimer;
        }).getSavedTimer();
    }

    public void initializeClocks(final LocalTime mainClock) {
        this.scrollHoursClock   = LocalTime.of(mainClock.getHour(), (int)MIN, (int)MIN);
        this.scrollMinutesClock = LocalTime.of((int)HR, mainClock.getMinute(), (int)MIN);
        this.scrollSecondsClock = LocalTime.of((int)HR, (int)MIN, mainClock.getSecond());

        eventSeconds.onNext(this.tick.apply(isDeltaGreaterThan, 1).apply(scrollSecondsClock, 2).getSecond());
        eventSeconds.onNext(this.tick.apply(isDeltaGreaterThan, 1).apply(scrollMinutesClock, 2).getMinute());
        eventSeconds.onNext(this.tick.apply(isDeltaGreaterThan, 1).apply(scrollHoursClock, 2).getHour());
//        eventSeconds.push(this.scrollSecondsClock.plusSeconds(2).getSecond());
//        eventMinutes.push(this.scrollMinutesClock.plusMinutes(2).getMinute());
//        eventHours.push(this.scrollHoursClock.plusHours(2).getHour());
    }

    public LocalTime getMainClock() {
        return this.mainClock;
    }

    public Observable<Integer> tick(Direction direction) {
        this.scrollSecondsClock = tick.apply(isDeltaGreaterThan, direction.getDelta()).apply(this.scrollSecondsClock, 1);
        this.mainClock = tick.apply(isDeltaGreaterThan, direction.getDelta()).apply(this.mainClock, 1);
//                this.scrollSecondsClock = pS.apply(this.scrollSecondsClock, normalizeDelta.apply(currentDelta.getDelta()));
//                this.mainClock = clockTick.apply(mainClock, currentDelta);
//                this.eventSeconds.push(pS.apply(this.scrollSecondsClock, normalizeDelta.apply(currentDelta.getDelta())).getSecond());
//        this.eventSeconds.onNext(this.scrollSecondsClock.getSecond());

        // Count down has ended.
        if (this.mainClock == LocalTime.of(0, 0, 0)) {
            this.stopCountdown.push(null);
        }

        if (this.scrollSecondsClock.getSecond() == MIN) {
            this.playMinutes.push(null);
        }

        return Observable.just(this.scrollSecondsClock.getSecond());
    }

    @Override
    public Map<ColumnType, Map<Integer, Integer>> initialize(Direction fromDirection) {
        Map<Integer, Integer> seconds = Map.of(0, mainClock.minusSeconds(1).getSecond(), 1, mainClock.getSecond(), 2, mainClock.plusSeconds(1).getSecond(), 3, mainClock.plusSeconds(2).getSecond());
        Map<Integer, Integer> minutes = Map.of(0, mainClock.minusMinutes(1).getMinute(), 1, mainClock.getMinute(), 2, mainClock.plusMinutes(1).getMinute(), 3, mainClock.plusMinutes(2).getMinute());
        Map<Integer, Integer> hours = Map.of(0, mainClock.minusHours(1).getHour(), 1, mainClock.getHour(), 2, mainClock.plusHours(1).getHour(), 3, mainClock.plusHours(2).getHour());
        return Map.of(ColumnType.SECONDS, seconds, ColumnType.MINUTES, minutes, ColumnType.HOURS, hours);
    }
}
