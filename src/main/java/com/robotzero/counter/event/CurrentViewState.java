package com.robotzero.counter.event;

import com.robotzero.counter.event.result.CurrentViewData;

public class CurrentViewState {
    private final boolean failure;
    private final String errorMessage;
    private final boolean start;
    private final boolean stop;
    private final CurrentViewData data;

    public CurrentViewState(boolean failure, boolean start, boolean stop, String errorMessage, CurrentViewData data) {
        this.start = start;
        this.stop = stop;
        this.failure = failure;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public static CurrentViewState start(CurrentViewData currentViewData) {
        return new CurrentViewState(false, true, false, "", currentViewData);
    }

    public static CurrentViewState stop(CurrentViewData currentViewData) {
        return new CurrentViewState(false, false, true, "", currentViewData);
    }

    public static CurrentViewState failure(String errorMessage) {
        return new CurrentViewState(false, false, false, errorMessage, null);
    }

    public static CurrentViewState idle() {
        return new CurrentViewState(false, false, false, "", null);
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
}