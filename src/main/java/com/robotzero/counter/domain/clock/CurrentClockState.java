package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.CellState;

import java.time.LocalTime;
import java.util.List;

public class CurrentClockState {

    private final Integer second;
    private final Integer minute;
    private final Integer hour;
    private final boolean tickSecond;
    private final boolean tickMinute;
    private final boolean tickHour;
    private final List<CellState> cellStates;
    private final LocalTime mainClockState;

    public CurrentClockState(Integer second, Integer minute, Integer hour, boolean tickSecond, boolean tickMinute, boolean tickHour, List<CellState> cellStates, LocalTime mainClockState) {
        this.second = second;
        this.minute = minute;
        this.hour = hour;
        this.tickSecond = tickSecond;
        this.tickMinute = tickMinute;
        this.tickHour = tickHour;
        this.cellStates = cellStates;
        this.mainClockState = mainClockState;
    }

    public Integer getSecond() {
        return second;
    }

    public Integer getMinute() {
        return minute;
    }

    public Integer getHour() {
        return hour;
    }

    public boolean shouldTickSecond() {
        return tickSecond;
    }

    public boolean shouldTickMinute() {
        return tickMinute;
    }

    public boolean shouldTickHour() {
        return tickHour;
    }

    public List<CellState> getCellStates() {
        return cellStates;
    }

    public LocalTime getMainClockState() {
        return mainClockState;
    }

    @Override
    public String toString() {
        return "CurrentClockState{" +
                "second=" + second +
                ", minute=" + minute +
                ", hour=" + hour +
                ", tickSecond=" + tickSecond +
                ", tickMinute=" + tickMinute +
                ", tickHour=" + tickHour +
                ", mainClock=" + mainClockState +
                '}';
    }
}
