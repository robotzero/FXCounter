package com.robotzero.counter.event.result;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.TimerType;
import com.robotzero.counter.domain.clock.CurrentClockState;
import javafx.util.Duration;

import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class TickResult implements Result {
    private final CurrentClockState currentClockState;
    private final TimerType timerType;
    private final ChronoUnit chronoUnit;
    private final ChronoField chronoField;

    public TickResult(final CurrentClockState currentClockState, final ChronoUnit chronoUnit, final ChronoField chronoField, final TimerType timerType) {
        this.currentClockState = currentClockState;
        this.timerType = timerType;
        this.chronoUnit = chronoUnit;
        this.chronoField = chronoField;
    }

    public CurrentClockState getLabels() {
        return this.currentClockState;
    }

    public TimerType getTimerType() {
        return timerType;
    }

    public Duration getDuration() {
        if (timerType.equals(TimerType.SCROLL)) {
            return Duration.millis(200);
        }

        if (timerType.equals(TimerType.TICK)) {
            return Duration.millis(600);
        }

        return Duration.millis(10);
    }

    @Override
    public String toString() {
        return "TickResult{" +
                ", currentClockState=" + currentClockState.toString() +
                ", columnUnit=" + chronoUnit +
                ", timerType=" + timerType +
                '}';
    }

    public ChronoUnit getChronoUnit() {
        return chronoUnit;
    }

    public ChronoField getChronoField() {
        return chronoField;
    }
}
