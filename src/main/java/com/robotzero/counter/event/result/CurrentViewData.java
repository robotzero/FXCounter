package com.robotzero.counter.event.result;

public class CurrentViewData {
    private final ClickResult clickResult;
    private final ScrollResult scrollResult;
    private final TickResult tickResult;

    public CurrentViewData(ClickResult clickResult, ScrollResult scrollResult, TickResult tickResult) {
        this.clickResult = clickResult;
        this.scrollResult = scrollResult;
        this.tickResult = tickResult;
    }

    public ClickResult getClickResult() {
        return clickResult;
    }

    public ScrollResult getScrollResult() {
        return scrollResult;
    }

    public TickResult getTickResult() {
        return tickResult;
    }
}
