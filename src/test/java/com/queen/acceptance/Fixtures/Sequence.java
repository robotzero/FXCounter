package com.queen.acceptance.Fixtures;

import com.queen.counter.domain.ColumnType;
import javafx.geometry.VerticalDirection;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Sequence {

    public List<Step> steps;
    public LocalTime clockStartState;
    public ExpectedValues expectedValues;

    private Sequence(SequenceBuilder builder) {
        this.expectedValues = builder.expectedValues;
        this.steps = builder.steps;
        this.clockStartState = builder.startClock;
    }

    public static Sequence create(Function<AddStep, Close> configuration) {
        return configuration.andThen(Close::create).apply(new SequenceBuilder());
    }

    public static interface AddStep {
        AddExpectedValues addStep(ColumnType columnType, VerticalDirection direction, Integer scrollsNumber);
    }

    public static interface AddClockStart {
        AddClockStart withStartClock(LocalTime clock);
        Close close();
    }

    public static interface AddExpectedValues {
        AddClockStart withExpectedValues(
                Integer topPositionSecondsMultiplier,
                String topLabelSeconds,
                Integer bottomPositionSecondsMultiplier,
                String bottomLabelSeconds,
                Integer topPositionMinutesMultiplier,
                String topLabelMinutes,
                Integer bottomPositionMinutesMultiplier,
                String bottomLabelMinutes
        );
    }

    public static interface Close {
        Sequence create();
    }

    static class Step {
        ColumnType columnType;
        VerticalDirection direction;
        Integer scrollsNumber;

        Step (ColumnType columnType, VerticalDirection direction, Integer scrollsNumber) {
            this.columnType = columnType;
            this.direction = direction;
            this.scrollsNumber = scrollsNumber;
        }
    }

    static class ExpectedValues {
        String topLabelSeconds;
        String bottomLabelSeconds;
        Integer topPositionSecondsMultiplier;
        Integer bottomPositionSecondsMultiplier;

        String topLabelMinutes;
        String bottomLabelMinutes;
        Integer topPositionMinutesMultiplier;
        Integer bottomPositionMinutesMultiplier;

        ExpectedValues (
                Integer topPositionSecondsMultiplier,
                String topLabelSeconds,
                Integer bottomPositionSecondsMultiplier,
                String bottomLabelSeconds,
                Integer topPositionMinutesMultiplier,
                String topLabelMinutes,
                Integer bottomPositionMinutesMultiplier,
                String bottomLabelMinutes
        ) {
            this.bottomLabelSeconds = bottomLabelSeconds;
            this.topLabelSeconds = topLabelSeconds;
            this.topPositionSecondsMultiplier = topPositionSecondsMultiplier;
            this.bottomPositionSecondsMultiplier = bottomPositionSecondsMultiplier;

            this.bottomLabelMinutes = bottomLabelMinutes;
            this.topLabelMinutes = topLabelMinutes;
            this.topPositionMinutesMultiplier = topPositionMinutesMultiplier;
            this.bottomPositionMinutesMultiplier = bottomPositionMinutesMultiplier;
        }
    }

    private static class SequenceBuilder implements AddStep, AddExpectedValues, AddClockStart, Close {

        final List<Step> steps = new ArrayList<>();
        private ExpectedValues expectedValues;
        private LocalTime startClock;

        @Override
        public AddExpectedValues addStep(ColumnType columnType, VerticalDirection direction, Integer scrollsNumber) {
            Step step = new Step(columnType, direction, scrollsNumber);
            steps.add(step);
            return this;
        }

        @Override
        public AddClockStart withExpectedValues(
                Integer topPositionSecondsMultiplier,
                String topLabelSeconds,
                Integer bottomPositionSecondsMultiplier,
                String bottomLabelSeconds,
                Integer topPositionMinutesMultiplier,
                String topLabelMinutes,
                Integer bottomPositionMinutesMultiplier,
                String bottomLabelMinutes
        ) {
            this.expectedValues = new ExpectedValues(
                    topPositionSecondsMultiplier,
                    topLabelSeconds,
                    bottomPositionSecondsMultiplier,
                    bottomLabelSeconds,
                    topPositionMinutesMultiplier,
                    topLabelMinutes,
                    bottomPositionMinutesMultiplier,
                    bottomLabelMinutes
            );

            return this;
        }

        @Override
        public AddClockStart withStartClock(LocalTime clock) {
            this.startClock = clock;
            return this;
        }

        @Override
        public Close close() {
            return this;
        }

        @Override
        public Sequence create() {
            return new Sequence(this);
        }
    }
}
