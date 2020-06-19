package com.robotzero.counter.event;

import com.robotzero.counter.event.result.CurrentViewData;

public class CurrentViewState {
  private final ViewState viewState;
  private final String errorMessage;
  private final CurrentViewData data;

  private CurrentViewState(
    final ViewState viewState,
    final String errorMessage,
    final CurrentViewData data
  ) {
    this.viewState = viewState;
    this.errorMessage = errorMessage;
    this.data = data;
  }

  public static CurrentViewState start(final CurrentViewData currentViewData) {
    return new CurrentViewState(ViewState.START, "", currentViewData);
  }

  public static CurrentViewState stop(final CurrentViewData currentViewData) {
    return new CurrentViewState(ViewState.STOP, "", currentViewData);
  }

  public static CurrentViewState pause(final CurrentViewData currentViewData) {
    return new CurrentViewState(ViewState.PAUSE, "", currentViewData);
  }

  public static CurrentViewState tick(final CurrentViewData currentViewData) {
    return new CurrentViewState(ViewState.TICK, "", currentViewData);
  }

  public static CurrentViewState reset(final CurrentViewData currentViewData) {
    return new CurrentViewState(ViewState.RESET, "", currentViewData);
  }

  public static CurrentViewState scroll(final CurrentViewData currentViewData) {
    return new CurrentViewState(ViewState.SCROLL, "", currentViewData);
  }

  public static CurrentViewState failure(final String errorMessage) {
    return new CurrentViewState(ViewState.FAILURE, errorMessage, null);
  }

  public static CurrentViewState init(final CurrentViewData currentViewData) {
    return new CurrentViewState(ViewState.INIT, "", currentViewData);
  }

  public static CurrentViewState idle() {
    return new CurrentViewState(ViewState.IDLE, "", null);
  }

  public CurrentViewData getData() {
    return data;
  }

  public boolean isFailure() {
    return this.viewState == ViewState.FAILURE;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public boolean isStart() {
    return viewState == ViewState.START;
  }

  public boolean isStop() {
    return viewState == ViewState.STOP;
  }

  public boolean isPause() {
    return viewState == ViewState.PAUSE;
  }

  public boolean isReset() {
    return viewState == ViewState.RESET;
  }

  public boolean isTick() {
    return viewState == ViewState.TICK;
  }

  public boolean isScroll() {
    return viewState == ViewState.SCROLL;
  }

  public boolean isClick() {
    return (
      viewState == ViewState.PAUSE ||
      viewState == ViewState.START ||
      viewState == ViewState.STOP ||
      viewState == ViewState.RESET
    );
  }

  public boolean isInit() {
    return viewState == ViewState.INIT;
  }

  @Override
  public String toString() {
    return (
      "CurrentViewState{" +
      "state=" +
      viewState +
      ", errorMessage='" +
      errorMessage +
      '\'' +
      ", data=" +
      data +
      '}'
    );
  }
}

enum ViewState {
  FAILURE,
  START,
  STOP,
  PAUSE,
  RESET,
  TICK,
  SCROLL,
  INIT,
  IDLE,
}
