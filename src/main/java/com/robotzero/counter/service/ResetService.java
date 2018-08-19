package com.robotzero.counter.service;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.TimerType;
import com.robotzero.counter.domain.clock.CurrentClockState;
import com.robotzero.counter.event.ButtonState;
import com.robotzero.counter.event.ButtonType;
import com.robotzero.counter.event.action.Action;
import com.robotzero.counter.event.action.ActionType;
import com.robotzero.counter.event.action.ClickAction;
import com.robotzero.counter.event.action.TickAction;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

public class ResetService {

    private CurrentClockState currentClockState;
    private final long baseTop = 59;
    private final long baseMiddle = 30;
    private final long baseBottom = 1;
    private final long hoursTop = 23;
    private final long hoursMiddle = 12;
    private final long hoursBottom = 1;

    public ResetService(PublishSubject<CurrentClockState> currentClockStateObservable) {
        currentClockStateObservable.subscribe(currentClockState -> {
            this.currentClockState = currentClockState;
        });
    }

    private BiPredicate<Integer, Integer> lessThanOrEquals = (noOfTicks, timeToReset) -> noOfTicks + timeToReset <= baseMiddle;

    public Observable<? extends Action> getActions(ButtonType buttonType, ButtonState buttonState) {
        if (buttonType.equals(ButtonType.RIGHT)) {
            List<Long> tickNumber = howManyTicks();
            int seconds = lessThanOrEquals.test(Math.toIntExact(tickNumber.get(0)), timeToReset().getSecond()) ? Math.abs(Math.toIntExact(tickNumber.get(0))) : Math.abs(Math.toIntExact(tickNumber.get(0))) * -1;
            int minutes = lessThanOrEquals.test(Math.toIntExact(tickNumber.get(1)), timeToReset().getMinute()) ? Math.abs(Math.toIntExact(tickNumber.get(1))) : Math.abs(Math.toIntExact(tickNumber.get(1))) * -1;
            int hours = lessThanOrEquals.test(Math.toIntExact(tickNumber.get(2)), timeToReset().getHour()) ? Math.abs(Math.toIntExact(tickNumber.get(2))) : Math.abs(Math.toIntExact(tickNumber.get(2))) * -1;

            return Observable.create((emitter) -> {
                emitter.onNext(new ClickAction(ActionType.valueOf(buttonType.name()), buttonState));
                IntStream.range(0, Math.abs(seconds)).mapToObj(index -> {
                    return new TickAction(seconds, ColumnType.SECONDS, TimerType.RESET);
                }).forEach(emitter::onNext);
                IntStream.range(0, Math.abs(minutes)).mapToObj(index -> {
                    return new TickAction(minutes, ColumnType.MINUTES, TimerType.RESET);
                }).forEach(emitter::onNext);
                IntStream.range(0, Math.abs(hours)).mapToObj(index -> {
                    return new TickAction(hours, ColumnType.HOURS, TimerType.RESET);
                }).forEach(emitter::onNext);
            }).zipWith(Observable.interval(10, TimeUnit.MILLISECONDS), (observable, interval) -> (Action) observable);
        }
        return Observable.just(new ClickAction(
                        ActionType.valueOf(buttonType.name()),
                        buttonState
                )
        );
    }

    private List<Long> howManyTicks() {
        LocalTime toReset = timeToReset();
        CurrentClockState currentClockState = Optional.ofNullable(this.currentClockState).orElseGet(() -> new CurrentClockState(toReset.getSecond(), toReset.getMinute(), toReset.getHour(), null, false, false, false));
        long seconds = 0, minutes = 0, hours = 0;
        if (currentClockState.getSecond() != toReset.getSecond()) {
            seconds = ChronoUnit.SECONDS.between(toReset, toReset.withSecond(currentClockState.getSecond())) > baseMiddle ? baseBottom + (baseTop - ChronoUnit.SECONDS.between(toReset, toReset.withSecond(currentClockState.getSecond()).plusSeconds(1))) : ChronoUnit.SECONDS.between(toReset, toReset.withSecond(currentClockState.getSecond()).plusSeconds(1));
        }
        if (currentClockState.getMinute() != toReset.getMinute()) {
            minutes = ChronoUnit.MINUTES.between(toReset, toReset.withMinute(currentClockState.getMinute())) > baseMiddle ? baseBottom + (baseTop - ChronoUnit.MINUTES.between(toReset, toReset.withMinute(currentClockState.getMinute()).plusMinutes(1))) : ChronoUnit.MINUTES.between(toReset, toReset.withMinute(currentClockState.getMinute()).plusMinutes(1));
        }

        if (currentClockState.getHour() != toReset.getHour()) {
            hours = ChronoUnit.HOURS.between(toReset, toReset.withHour(currentClockState.getHour())) > hoursMiddle ? hoursBottom + (hoursTop - ChronoUnit.HOURS.between(toReset, toReset.withHour(currentClockState.getHour()).plusHours(1))) : ChronoUnit.HOURS.between(toReset, toReset.withHour(currentClockState.getHour()).plusHours(1));
        }
//        System.out.println("CURRENT " + currentClockState.getSecond());
        return Arrays.asList(seconds, minutes, hours);
    }

    private LocalTime timeToReset() {
        return LocalTime.of(22, 1, 10);
    }
}
