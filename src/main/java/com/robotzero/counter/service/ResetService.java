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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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

    public Observable<? extends Action> getActions(ButtonType buttonType, ButtonState buttonState) {
        if (buttonType.equals(ButtonType.RIGHT)) {

            List<Action> actions = new ArrayList<>();
            actions.add(new ClickAction(ActionType.valueOf(buttonType.name()), buttonState));
            List<Long> tickNumber = howManyTicks();
            int seconds = Math.toIntExact(tickNumber.get(0)) + timeToReset().getSecond() <= baseMiddle  ? Math.abs(Math.toIntExact(tickNumber.get(0))) : Math.abs(Math.toIntExact(tickNumber.get(0))) * -1;
            int minutes = Math.toIntExact(tickNumber.get(1)) + timeToReset().getMinute() <= baseMiddle ? Math.abs(Math.toIntExact(tickNumber.get(1))) : Math.abs(Math.toIntExact(tickNumber.get(1))) * -1;
            int hours = Math.toIntExact(tickNumber.get(2)) + timeToReset().getHour() <= baseMiddle ? Math.abs(Math.toIntExact(tickNumber.get(2))) : Math.abs(Math.toIntExact(tickNumber.get(2))) * -1;
//            System.out.println("Seconds "  + seconds);
//            System.out.println("Minutes" + minutes);
            actions.addAll(IntStream.range(0, Math.abs(seconds)).mapToObj(index -> {
                return new TickAction(seconds, ColumnType.SECONDS, TimerType.RESET);
            }).collect(Collectors.toList()));
            actions.addAll(IntStream.range(0, Math.abs(minutes)).mapToObj(index -> {
                return new TickAction(minutes, ColumnType.MINUTES, TimerType.RESET);
            }).collect(Collectors.toList()));
            actions.addAll(IntStream.range(0, Math.abs(hours)).mapToObj(index -> {
                return new TickAction(hours, ColumnType.HOURS, TimerType.RESET);
            }).collect(Collectors.toList()));

            return Observable.zip(Observable.fromIterable(actions), Observable.interval(500, TimeUnit.MILLISECONDS), (iterable, interval) -> iterable);
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
        long seconds, minutes, hours;
        if (currentClockState.getSecond() == toReset.getSecond()) {
            seconds = 0;
        } else {
            seconds = ChronoUnit.SECONDS.between(toReset, toReset.withSecond(currentClockState.getSecond())) > baseMiddle ? baseBottom + (baseTop - ChronoUnit.SECONDS.between(toReset, toReset.withSecond(currentClockState.getSecond()).plusSeconds(1))) : ChronoUnit.SECONDS.between(toReset, toReset.withSecond(currentClockState.getSecond()).plusSeconds(1));
        }
        if (currentClockState.getMinute() == toReset.getMinute()) {
            minutes = 0;
        } else {
            minutes = ChronoUnit.MINUTES.between(toReset, toReset.withMinute(currentClockState.getMinute())) > baseMiddle ? baseBottom + (baseTop - ChronoUnit.MINUTES.between(toReset, toReset.withMinute(currentClockState.getMinute()).plusMinutes(1))) : ChronoUnit.MINUTES.between(toReset, toReset.withMinute(currentClockState.getMinute()).plusMinutes(1));
        }

        if (currentClockState.getHour() == toReset.getHour()) {
            hours = 0;
        } else {
            hours = ChronoUnit.HOURS.between(toReset, toReset.withHour(currentClockState.getHour())) > hoursMiddle ? hoursBottom + (hoursTop - ChronoUnit.HOURS.between(toReset, toReset.withHour(currentClockState.getHour()).plusHours(1))) : ChronoUnit.HOURS.between(toReset, toReset.withHour(currentClockState.getHour()).plusHours(1));
        }
//        System.out.println("CURRENT " + currentClockState.getSecond());
        return Arrays.asList(seconds, minutes, hours);
    }

    private LocalTime timeToReset() {
        return LocalTime.of(22, 1, 10);
    }
}
