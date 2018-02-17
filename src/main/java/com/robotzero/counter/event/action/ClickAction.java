package com.robotzero.counter.event.action;

import com.robotzero.counter.event.ButtonState;

public class ClickAction implements Action {
    private final ActionType actionType;
    private final ButtonState buttonState;

    public ClickAction(ActionType actionType, ButtonState buttonState) {
        this.actionType = actionType;
        this.buttonState = buttonState;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public ButtonState getButtonState() {
        return buttonState;
    }

    public ButtonState getNewButtonState() {
        if (buttonState.equals(ButtonState.START)) {
            return ButtonState.PAUSE;
        } else if (buttonState.equals(ButtonState.PAUSE)) {
            return ButtonState.START;

        }

        throw new RuntimeException("Unknown button state.");
    }
}
