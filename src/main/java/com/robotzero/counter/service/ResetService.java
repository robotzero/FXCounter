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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
            return Observable.create(emitter -> {
                emitter.onNext(new ClickAction(ActionType.valueOf(buttonType.name()), buttonState));
                IntStream.range(0, howManyTicks().get(0)).forEach(index -> {
                   emitter.onNext(new TickAction(-40, ColumnType.SECONDS, TimerType.SCROLL));
                });
                IntStream.range(0, howManyTicks().get(1)).forEach(index -> {
                    emitter.onNext(new TickAction(-40, ColumnType.MINUTES, TimerType.SCROLL));
                });
                IntStream.range(0, howManyTicks().get(2)).forEach(index -> {
                    emitter.onNext(new TickAction(-40, ColumnType.HOURS, TimerType.SCROLL));
                });
                emitter.onComplete();
            });
//            return Observable.just(new ClickAction(
//                    ActionType.valueOf(buttonType.name()),
//                    buttonState
//            ), new TickAction(-40, ColumnType.SECONDS, TimerType.SCROLL));
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
