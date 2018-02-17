package com.robotzero.counter.service;

import com.robotzero.counter.event.action.ClickAction;
import io.reactivex.Flowable;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TimerService {
    private AtomicLong elapsedTime = new AtomicLong();
    private AtomicBoolean resumed = new AtomicBoolean();
    private AtomicBoolean stopped = new AtomicBoolean();
    private Flowable<Long> timer;

    public void startTimer() {
        resumed.set(true);
        stopped.set(false);
    }

    @PostConstruct
    public void initTimer() {
        this.timer = Flowable.interval(1, TimeUnit.SECONDS)
                .takeWhile(tick -> !stopped.get())
                .filter(tick -> resumed.get())
                .map(tick -> elapsedTime.addAndGet(1000));
    }

    public void operateTimer(ClickAction clickAction) {

    }

    public Flowable<Long> getTimer() {
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
