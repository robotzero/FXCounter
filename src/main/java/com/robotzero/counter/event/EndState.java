package com.robotzero.counter.event;

public class EndState {
    private final boolean success;
    private final boolean failure;
    private final String errorMessage;
    private final boolean start;
    private final boolean stop;
    protected final SubmitEvent data;

    public EndState(boolean success, boolean failure, boolean start, boolean stop, String errorMessage, SubmitEvent data) {
        this.success = success;
        this.start = start;
        this.stop = stop;
        this.failure = failure;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public static EndState success(SubmitEvent data) {
        return new EndState(true, false, false, false, "", data);
    }

    public static EndState start() {
        return new EndState(false, false, true, false, "", null);
    }

    public static EndState stop() {
        return new EndState(false, false, false, true, "", null);
    }

    public static EndState failure(String errorMessage) {
        return new EndState(false, false, false, false, errorMessage, null);
    }

    public static EndState idle() {
        return new EndState(false, false, false, false, "", null);
    }

    public SubmitEvent getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return failure;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isStop() {
        return stop;
    }
}