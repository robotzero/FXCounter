package com.robotzero.counter.event.result;

public class CurrentViewData {
    private final Result result;

    public CurrentViewData(final Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }
}
