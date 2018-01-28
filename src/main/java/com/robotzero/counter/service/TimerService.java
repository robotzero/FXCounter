package com.robotzero.counter.service;

import io.reactivex.Flowable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TimerService {
    AtomicLong elapsedTime = new AtomicLong();
    AtomicBoolean resumed = new AtomicBoolean();
    AtomicBoolean stopped = new AtomicBoolean();

    public void startTimer() {
        resumed.set(true);
        stopped.set(false);
    }

    public Flowable<Long> initTimer() {
        return Flowable.interval(1, 1, TimeUnit.SECONDS)
                .takeWhile(tick -> !stopped.get())
                .filter(tick -> resumed.get())
                .map(tick -> elapsedTime.addAndGet(1000));
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
