package com.robotzero.counter.event.action;

import com.robotzero.counter.event.ButtonState;

public class ClickAction implements Action {
    private final ActionType actionType;
    private final ButtonState buttonState;

    public ClickAction(final ActionType actionType, final ButtonState buttonState) {
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
        if (buttonState == ButtonState.START) {
            return ButtonState.PAUSE;
        }

        if (buttonState == ButtonState.PAUSE) {
            return ButtonState.START;
        }

        if (buttonState == ButtonState.RESET) {
            return ButtonState.RESET;
        }

        throw new RuntimeException("Unknown button state.");
    }
}
