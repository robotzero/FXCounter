package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.Direction;

import java.util.Map;

public class CurrentClockState {

    private final Integer second;
    private final Integer minute;
    private final Integer hour;
    private final Direction direction;
    private final boolean tickMinute;
    private final boolean tickHour;

    public CurrentClockState(Integer second, Integer minute, Integer hour, Direction direction, boolean tickMinute, boolean tickHour) {
        this.second = second;
        this.minute = minute;
        this.hour = hour;
        this.direction = direction;
        this.tickMinute = tickMinute;
        this.tickHour = tickHour;
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

    public boolean shouldTickMinute() {
        return tickMinute;
    }

    public boolean shouldTickHour() {
        return tickHour;
    }

    public Direction getDirection() {
        return direction;
    }
}
