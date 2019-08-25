package com.robotzero.counter.event.action;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Tick;
import com.robotzero.counter.domain.TimerType;

import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Set;

public class TickAction implements Action {

    private final double delta;
    private final TimerType timerType;
    private final Set<Tick> tickData;

    public TickAction(
            final double delta,
            final Set<Tick> tickData,
            final TimerType timerType
    ) {
        this.delta = delta;
        this.tickData = tickData;
        this.timerType = timerType;
    }

    public TimerType getTimerType() {
        return timerType;
    }

    public double getDelta() {
        return delta;
    }

    public Set<Tick> getTickData(){
        return this.tickData;
    }

    public TickAction with(ColumnType columnType, ChronoField chronoField, ChronoUnit chronoUnit) {
        Set<Tick> tickData = Set.copyOf(this.tickData);
        tickData.add(new Tick(columnType, chronoUnit, chronoField));
        return new TickAction(delta,tickData, timerType);
    }
}
