package com.robotzero.counter.service;

import com.robotzero.counter.domain.*;
import com.robotzero.counter.domain.clock.CurrentClockState;
import com.robotzero.counter.event.ButtonState;
import com.robotzero.counter.event.ButtonType;
import com.robotzero.counter.event.action.Action;
import com.robotzero.counter.event.action.ActionType;
import com.robotzero.counter.event.action.ClickAction;
import com.robotzero.counter.event.action.TickAction;
import io.reactivex.Observable;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ResetService {

    private final long baseTop = 59;
    private final long baseMiddle = 30;
    private final long baseBottom = 1;
    private final long hoursTop = 23;
    private final long hoursMiddle = 12;
    private final long hoursBottom = 1;

    public Observable<? extends Action> getActions(ButtonType buttonType, ButtonState buttonState) {
        if (buttonType.equals(ButtonType.RIGHT)) {
            List<Long> tickNumber = howManyTicks();
            int seconds = Math.toIntExact(tickNumber.get(0) * -1);
            int minutes = Math.toIntExact(tickNumber.get(1) * -1);
            int hours = Math.toIntExact(tickNumber.get(2) * -1);
            //@TODO change that to one Action, at set the main clock and settings clock here?
            //@TODO Animation needs to finish before another can start playing so we do not need to slow down.
            return Observable.create((emitter) -> {
                emitter.onNext(new ClickAction(ActionType.valueOf(buttonType.name()), buttonState));
                IntStream.range(0, Math.abs(seconds)).mapToObj(index -> {
                    return new TickAction(seconds, Set.of(new Tick(ColumnType.SECONDS, ChronoUnit.SECONDS, ChronoField.SECOND_OF_MINUTE)), TimerType.RESET);
                }).forEach(emitter::onNext);
                IntStream.range(0, Math.abs(minutes)).mapToObj(index -> {
                    return new TickAction(minutes, Set.of(new Tick(ColumnType.MINUTES, ChronoUnit.MINUTES, ChronoField.SECOND_OF_MINUTE)) ,TimerType.RESET);
                }).forEach(emitter::onNext);
                IntStream.range(0, Math.abs(hours)).mapToObj(index -> {
                    return new TickAction(hours, Set.of(new Tick(ColumnType.HOURS, ChronoUnit.HOURS, ChronoField.HOUR_OF_DAY)), TimerType.RESET);
                }).forEach(emitter::onNext);
            }).zipWith(Observable.interval(60, TimeUnit.MILLISECONDS), (observable, interval) -> (Action) observable);
        }
        return Observable.just(new ClickAction(
                        ActionType.valueOf(buttonType.name()),
                        buttonState
                )
        );
    }

    private List<Long> howManyTicks() {
        LocalTime toReset = timeToReset();
//        CurrentClockState currentClockState = Optional.ofNullable(this.currentClockState).orElseGet(() -> new CurrentClockState(toReset.getSecond(), toReset.getMinute(), toReset.getHour(), null, null));
        long seconds = 0, minutes = 0, hours = 0;
//        if (currentClockState.getMainClockState().getSecond() != toReset.getSecond()) {
//            seconds = ChronoUnit.SECONDS.between(toReset, toReset.withSecond(currentClockState.getMainClockState().getSecond())) > baseMiddle ? -1 * (baseBottom + (baseTop - ChronoUnit.SECONDS.between(toReset, toReset.withSecond(currentClockState.getMainClockState().getSecond())))) : ChronoUnit.SECONDS.between(toReset, toReset.withSecond(currentClockState.getMainClockState().getSecond()));
//        }
//        if (currentClockState.getMainClockState().getMinute() != toReset.getMinute()) {
//            minutes = ChronoUnit.MINUTES.between(toReset, toReset.withMinute(currentClockState.getMainClockState().getMinute())) > baseMiddle ? -1 * (baseBottom + (baseTop - ChronoUnit.MINUTES.between(toReset, toReset.withMinute(currentClockState.getMainClockState().getMinute())))) : ChronoUnit.MINUTES.between(toReset, toReset.withMinute(currentClockState.getMainClockState().getMinute()));
//        }
//
//        if (currentClockState.getMainClockState().getHour() != toReset.getHour()) {
//            hours = ChronoUnit.HOURS.between(toReset, toReset.withHour(currentClockState.getMainClockState().getHour())) > hoursMiddle ? -1 * (hoursBottom + (hoursTop - ChronoUnit.HOURS.between(toReset, toReset.withHour(currentClockState.getMainClockState().getHour())))) : ChronoUnit.HOURS.between(toReset, toReset.withHour(currentClockState.getMainClockState().getHour()));
//        }
        return Arrays.asList(seconds, minutes, hours);
    }

    private LocalTime timeToReset() {
        return LocalTime.of(22, 1, 10);
    }
}
