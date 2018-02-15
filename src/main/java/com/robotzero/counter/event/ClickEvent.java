package com.robotzero.counter.event;

public class ClickEvent implements SubmitEvent {
    private final ButtonType buttonType;
    private final ButtonState buttonState;

    public ClickEvent(ButtonType buttonType, ButtonState buttonState) {
        this.buttonType = buttonType;
        this.buttonState = buttonState;
    }

    public ButtonType getButtonType() {
        return buttonType;
    }

    public ButtonState getButtonState() {
        return buttonState;
    }
}
