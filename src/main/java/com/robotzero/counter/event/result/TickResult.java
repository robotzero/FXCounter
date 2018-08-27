package com.robotzero.counter.event.result;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.TimerType;
import com.robotzero.counter.domain.clock.CurrentClockState;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TickResult implements Result {
    private final Text secondsCell;
    private final Text minutesCell;
    private final Text hoursCell;
    private final CurrentClockState currentClockState;
    private final ColumnType columnType;
    private final TimerType timerType;

    public TickResult(Text secondsCell, Text minutesCell, Text hoursCell, CurrentClockState currentClockState, ColumnType columnType, TimerType timerType) {
        this.secondsCell = secondsCell;
        this.minutesCell = minutesCell;
        this.hoursCell = hoursCell;
        this.currentClockState = currentClockState;
        this.columnType = columnType;
        this.timerType = timerType;
    }

    public CurrentClockState getLabels() {
        return this.currentClockState;
    }

    public Text getLabel(ColumnType columnType) {
        if (columnType.equals(ColumnType.SECONDS)) {
            return getSecondsCell();
        }

        if (columnType.equals(ColumnType.MINUTES)) {
            return getMinutesCell();
        }

        if (columnType.equals(ColumnType.HOURS)) {
            return getHoursCell();
        }

        throw new RuntimeException("Unsupported column type.");
    }

    public Text getSecondsCell() {
        return secondsCell;
    }

    public Text getMinutesCell() {
        return minutesCell;
    }

    public Text getHoursCell() {
        return hoursCell;
    }

    public ColumnType getColumnType() {
        return columnType;
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
                "secondsCell=" + secondsCell +
                ", minutesCell=" + minutesCell +
                ", hoursCell=" + hoursCell +
                ", currentClockState=" + currentClockState.toString() +
                ", columnType=" + columnType +
                ", timerType=" + timerType +
                '}';
    }
}
