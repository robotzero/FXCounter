package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.CellState;

import java.time.LocalTime;
import java.util.List;

public class CurrentClockState {

    private final Integer second;
    private final Integer minute;
    private final Integer hour;
    private final List<CellState> cellStates;
    private final LocalTime mainClockState;

    public CurrentClockState(Integer second, Integer minute, Integer hour, List<CellState> cellStates, LocalTime mainClockState) {
        this.second = second;
        this.minute = minute;
        this.hour = hour;
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
                ", mainClock=" + mainClockState +
                '}';
    }
}
