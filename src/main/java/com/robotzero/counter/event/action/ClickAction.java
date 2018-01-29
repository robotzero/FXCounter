package com.robotzero.counter.event.action;

public class ClickAction implements Action {
    private ActionType actionType;

    public ClickAction(ActionType actionType) {
        this.actionType = actionType;
    }

    public ActionType getActionType() {
        return actionType;
    }
}
