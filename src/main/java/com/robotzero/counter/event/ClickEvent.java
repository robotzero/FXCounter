package com.robotzero.counter.event;

public class ClickEvent implements SubmitEvent {
    private ButtonType buttonType;

    public ClickEvent(ButtonType buttonType) {
        this.buttonType = buttonType;
    }

    public ButtonType getButtonType() {
        return buttonType;
    }
}
