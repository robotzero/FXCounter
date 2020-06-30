package com.robotzero.counter.service;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.clock.ClockRepository;
import com.robotzero.counter.domain.clock.TimerRepository;
import com.robotzero.counter.event.ButtonState;
import com.robotzero.counter.event.action.ClickAction;
import io.reactivex.rxjava3.core.Observable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TimerService {
  private final TimerRepository timerRepository;
  private final ClockRepository clockRepository;
  private AtomicLong elapsedTime = new AtomicLong();
  private AtomicBoolean resumed = new AtomicBoolean();
  private AtomicBoolean stopped = new AtomicBoolean();

  public TimerService(TimerRepository timerRepository, ClockRepository clockRepository) {
    this.timerRepository = timerRepository;
    this.clockRepository = clockRepository;
  }

  private void startTimer() {
    resumed.set(true);
    stopped.set(false);
  }

  public Observable<ClickAction> operateTimer(ClickAction clickAction) {
    return Observable.fromCallable(
      () -> {
        if (clickAction.getButtonState().equals(ButtonState.START)) {
          timerRepository.create("latest", clockRepository.get(ColumnType.MAIN));
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
      }
    );
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

  public AtomicBoolean resumed() {
    return this.resumed;
  }

  public AtomicLong elapsedTime() {
    return this.elapsedTime;
  }
}
