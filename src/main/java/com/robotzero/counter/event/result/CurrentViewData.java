package com.robotzero.counter.event.result;

public class CurrentViewData {
    private final ClickResult clickResult;
    private final ScrollResult scrollResult;
    private final TickResult tickResult;
    private final InitViewResult initViewResult;

    public CurrentViewData(ClickResult clickResult, ScrollResult scrollResult, TickResult tickResult, InitViewResult initViewResult) {
        this.clickResult = clickResult;
        this.scrollResult = scrollResult;
        this.tickResult = tickResult;
        this.initViewResult = initViewResult;
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

    public InitViewResult getInitViewResult() {
        return initViewResult;
    }
}
