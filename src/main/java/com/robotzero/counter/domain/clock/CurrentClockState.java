package com.robotzero.counter.domain.clock;

import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.DirectionType;
import javafx.scene.text.Text;

public class CurrentClockState {

    private final Integer second;
    private final Integer minute;
    private final Integer hour;
    private final Direction directionSeconds;
    private final Direction directionMinutes;
    private final Direction directionHours;
    private final boolean tickSecond;
    private final boolean tickMinute;
    private final boolean tickHour;
    private final Text labelSeconds;
    private final Text labelMinutes;
    private final Text labelHours;

    public CurrentClockState(Integer second, Integer minute, Integer hour, Direction directionSeconds, Direction directionMinutes, Direction directionHours, boolean tickSecond, boolean tickMinute, boolean tickHour, Text labelSeconds, Text labelMinutes, Text labelHours) {
        this.second = second;
        this.minute = minute;
        this.hour = hour;
        this.directionSeconds = directionSeconds;
        this.directionMinutes = directionMinutes;
        this.directionHours = directionHours;
        this.tickSecond = tickSecond;
        this.tickMinute = tickMinute;
        this.tickHour = tickHour;
        this.labelSeconds = labelSeconds;
        this.labelMinutes = labelMinutes;
        this.labelHours = labelHours;
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

    public Direction getDirectionSeconds() {
        return this.directionSeconds;
    }

    public Direction getDirectionMinutes() {
        return directionMinutes;
    }

    public Direction getDirectionHours() {
        return directionHours;
    }

    public Text getLabelSeconds() {
        return labelSeconds;
    }

    public Text getLabelMinutes() {
        return labelMinutes;
    }

    public Text getLabelHours() {
        return labelHours;
    }

    @Override
    public String toString() {
        return "CurrentClockState{" +
                "second=" + second +
                ", minute=" + minute +
                ", hour=" + hour +
                ", directionSeconds=" + directionSeconds +
                ", directionMinutes=" + directionMinutes +
                ", directionHours=" + directionHours +
                ", tickSecond=" + tickSecond +
                ", tickMinute=" + tickMinute +
                ", tickHour=" + tickHour +
                '}';
    }
}
