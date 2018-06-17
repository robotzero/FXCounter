package com.robotzero.counter.service;

import com.robotzero.counter.event.ButtonState;
import com.robotzero.counter.event.action.ClickAction;
import io.reactivex.Observable;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TimerService {
    private AtomicLong elapsedTime = new AtomicLong();
    private AtomicBoolean resumed = new AtomicBoolean();
    private AtomicBoolean stopped = new AtomicBoolean();
    private Observable<Long> timer;

    public void startTimer() {
        resumed.set(true);
        stopped.set(false);
    }

    @PostConstruct
    public void initTimer() {
        this.timer = Observable.interval(1, TimeUnit.SECONDS)
//                .takeWhile(tick -> !stopped.get())
                .filter(tick -> resumed.get())
                .map(tick -> elapsedTime.addAndGet(1000));
    }

    public Observable<ClickAction> operateTimer(ClickAction clickAction) {
        return Observable.fromCallable(() -> {
            if (clickAction.getButtonState().equals(ButtonState.START)) {
                this.startTimer();
            }

            if (clickAction.getButtonState().equals(ButtonState.PAUSE)) {
                this.pauseTimer();
            }

            if (clickAction.getButtonState().equals(ButtonState.STOP)) {
                this.stopTimer();
            }

            if (clickAction.getButtonState().equals(ButtonState.RESET)) {
                this.pauseTimer();
            }

            return clickAction;
        });
    }

    public Observable<Long> getTimer() {
        return timer;
    }

    public void pauseTimer() {
        resumed.set(false);
    }

    public void resumeTimer() {
        resumed.set(true);
    }

    public void stopTimer() {
        stopped.set(true);
    }

    public void addToTimer(int seconds) {
        elapsedTime.addAndGet(seconds * 1000);
    }
}
