package com.robotzero.counter.event.result;

public class CurrentViewData {
    private final ClickResult clickResult;
    private final ScrollResult scrollResult;

    public CurrentViewData(ClickResult clickResult, ScrollResult scrollResult) {
        this.clickResult = clickResult;
        this.scrollResult = scrollResult;
    }

    public ClickResult getClickResult() {
        return clickResult;
    }

    public ScrollResult getScrollResult() {
        return scrollResult;
    }
}
