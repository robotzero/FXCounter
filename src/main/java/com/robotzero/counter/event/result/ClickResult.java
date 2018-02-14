package com.robotzero.counter.event.result;

import com.robotzero.counter.event.action.ActionType;

public class ClickResult implements Result {
    private final ActionType actionType;

    public ClickResult(ActionType actionType) {
        this.actionType = actionType;
    }

    public ActionType getActionType() {
        return actionType;
    }
}
