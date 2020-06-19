package com.robotzero.counter.event.result;

import com.robotzero.counter.event.ButtonState;
import com.robotzero.counter.event.action.ActionType;

public class ClickResult implements Result {
  private final ActionType actionType;
  private final ButtonState buttonState;

  public ClickResult(ActionType actionType, ButtonState buttonState) {
    this.actionType = actionType;
    this.buttonState = buttonState;
  }

  public ActionType getActionType() {
    return actionType;
  }

  public ButtonState getButtonState() {
    return buttonState;
  }
}
