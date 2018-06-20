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

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.toIntExact;

public class ResetService {

    private CurrentClockState currentClockState;

    public ResetService(PublishSubject<CurrentClockState> currentClockStateObservable) {
        currentClockStateObservable.subscribe(currentClockState -> {
            this.currentClockState = currentClockState;
        });
    }

    public Observable<? extends Action> getActions(ButtonType buttonType, ButtonState buttonState) {
        howManyTicks();
        if (buttonType.equals(ButtonType.RIGHT)) {
            List<Action> actions = new ArrayList<>();
            actions.add(new ClickAction(ActionType.valueOf(buttonType.name()), buttonState));
            List<Integer> tickNumber = howManyTicks();
            int seconds = tickNumber.get(0);
            int minutes = tickNumber.get(1);
            int hours = tickNumber.get(2);
            actions.addAll(IntStream.range(0, Math.abs(seconds)).mapToObj(index -> {
                return new TickAction(seconds, ColumnType.SECONDS, TimerType.SCROLL);
            }).collect(Collectors.toList()));
            actions.addAll(IntStream.range(0, Math.abs(minutes)).mapToObj(index -> {
                return new TickAction(minutes, ColumnType.MINUTES, TimerType.SCROLL);
            }).collect(Collectors.toList()));
            actions.addAll(IntStream.range(0, Math.abs(hours)).mapToObj(index -> {
                return new TickAction(hours, ColumnType.HOURS, TimerType.SCROLL);
            }).collect(Collectors.toList()));

            return Observable.zip(Observable.fromIterable(actions), Observable.interval(100, TimeUnit.MILLISECONDS), (left, right) -> left);
        }
        return Observable.just(new ClickAction(
                        ActionType.valueOf(buttonType.name()),
                        buttonState
                )
        );
    }

    private List<Integer> howManyTicks() {
        CurrentClockState currentClockState1 = Optional.ofNullable(this.currentClockState).orElseGet(() -> new CurrentClockState(timeToReset().getSecond(), timeToReset().getMinute(), timeToReset().getHour(), null, false, false, false));
        LocalTime toReset = timeToReset();
        int elapsedSeconds = toIntExact(Duration.between(timeToReset(), LocalTime.of(toReset.getHour(), toReset.getMinute(), currentClockState1.getSecond())).toSeconds());
        int elapsedMinutes = toIntExact(Duration.between(timeToReset(), LocalTime.of(toReset.getHour(), currentClockState1.getMinute(), toReset.getSecond())).toMinutes());
        int elapsedHours = toIntExact(Duration.between(timeToReset(), LocalTime.of(currentClockState1.getHour(), toReset.getMinute(), toReset.getSecond())).toHours());
        return Arrays.asList(elapsedSeconds, elapsedMinutes, elapsedHours);
    }

    private LocalTime timeToReset() {
        return LocalTime.of(22, 01, 10);
    }
}
