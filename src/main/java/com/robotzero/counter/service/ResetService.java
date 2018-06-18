package com.robotzero.counter.service;

import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.TimerType;
import com.robotzero.counter.event.ButtonState;
import com.robotzero.counter.event.ButtonType;
import com.robotzero.counter.event.action.Action;
import com.robotzero.counter.event.action.ActionType;
import com.robotzero.counter.event.action.ClickAction;
import com.robotzero.counter.event.action.TickAction;
import io.reactivex.Observable;

public class ResetService {
    public Observable<? extends Action> getActions(ButtonType buttonType, ButtonState buttonState) {
        if (buttonType.equals(ButtonType.RIGHT)) {
            return Observable.just(new ClickAction(
                    ActionType.valueOf(buttonType.name()),
                    buttonState
            ), new TickAction(-40, ColumnType.SECONDS, TimerType.SCROLL));
        }
        return Observable.just(new ClickAction(
                        ActionType.valueOf(buttonType.name()),
                        buttonState
                )
        );
    }
}
