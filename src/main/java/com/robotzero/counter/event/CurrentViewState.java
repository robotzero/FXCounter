package com.robotzero.counter.event;

import com.robotzero.counter.event.result.CurrentViewData;

public class CurrentViewState {
    private final boolean failure;
    private final String errorMessage;
    private final boolean click;
    private final boolean start;
    private final boolean stop;
    private final boolean pause;
    private final boolean reset;
    private final boolean tick;
    private final boolean scroll;
    private final CurrentViewData data;

    public CurrentViewState(boolean failure, boolean click, boolean start, boolean stop, boolean pause, boolean reset, boolean tick, boolean scroll, String errorMessage, CurrentViewData data) {
        this.click = click;
        this.start = start;
        this.stop = stop;
        this.pause = pause;
        this.reset = reset;
        this.tick = tick;
        this.scroll = scroll;
        this.failure = failure;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public static CurrentViewState start(CurrentViewData currentViewData) {
        return new CurrentViewState(false, true, true, false, false, false, false, false, "", currentViewData);
    }

    public static CurrentViewState stop(CurrentViewData currentViewData) {
        return new CurrentViewState(false, true, false, true, false, false, false, false, "", currentViewData);
    }

    public static CurrentViewState pause(CurrentViewData currentViewData) {
        return new CurrentViewState(false, true, false, false, true, false, false, false, "", currentViewData);
    }

    public static CurrentViewState tick(CurrentViewData currentViewData) {
        return new CurrentViewState(false, false, false, false, false, false, true, false, "", currentViewData);
    }

    public static CurrentViewState reset(CurrentViewData currentViewData) {
        return new CurrentViewState(false, true, false, false, false, true, false, false, "", currentViewData);
    }

    public static CurrentViewState scroll(CurrentViewData currentViewData) {
        return new CurrentViewState(false, false, false, false, false, false, false, true, "", currentViewData);
    }

    public static CurrentViewState failure(String errorMessage) {
        return new CurrentViewState(false, false, false, false, false, false, false, false, errorMessage, null);
    }

    public static CurrentViewState idle() {
        return new CurrentViewState(false, false, false, false, false, false, false, false, "", null);
    }

    public CurrentViewData getData() {
        return data;
    }

    public boolean isFailure() {
        return failure;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isStop() {
        return stop;
    }

    public boolean isPause() {
        return pause;
    }

    public boolean isReset() {
        return reset;
    }

    public boolean isTick() {
        return tick;
    }

    public boolean isScroll() {
        return scroll;
    }

    public boolean isClick() {
        return click;
    }

    @Override
    public String toString() {
        return "CurrentViewState{" +
                "failure=" + failure +
                ", errorMessage='" + errorMessage + '\'' +
                ", click=" + click +
                ", start=" + start +
                ", stop=" + stop +
                ", pause=" + pause +
                ", reset=" + reset +
                ", scroll=" + scroll +
                ", data=" + data +
                '}';
    }
}