package com.robotzero.counter.event.action;

import com.robotzero.counter.domain.TimerType;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class TickAction implements Action {

    private final double delta;
    private final TimerType timerType;
    private final ChronoField chronoField;
    private final ChronoUnit chronoUnit;

    public TickAction(
            final double delta,
            final ChronoUnit chronoUnit,
            final ChronoField chronoField,
            final TimerType timerType
    ) {
        this.delta = delta;
        this.chronoUnit = chronoUnit;
        this.chronoField = chronoField;
        this.timerType = timerType;
    }

    public TimerType getTimerType() {
        return timerType;
    }

    public double getDelta() {
        return delta;
    }

    public ChronoField getChronoField() {
        return chronoField;
    }

    public ChronoUnit getChronoUnit() {
        return chronoUnit;
    }
}
