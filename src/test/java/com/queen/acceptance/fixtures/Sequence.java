package com.queen.acceptance.fixtures;

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

    public static Sequence create(Function<AddClockStart, Close> configuration) {
        return configuration.andThen(Close::create).apply(new SequenceBuilder());
    }

    public static interface AddStep {
        AddStep addStep(ColumnType columnType, VerticalDirection direction, Integer scrollsNumber);
        Close close();
    }

    public static interface AddClockStart {
        AddExpectedValues withStartClock(LocalTime clock);
    }

    public static interface AddExpectedValues {
        AddStep withExpectedValues(
                Integer topPositionSecondsMultiplier,
                String topLabelSeconds,
                Integer bottomPositionSecondsMultiplier,
                String bottomLabelSeconds,
                Integer topPositionMinutesMultiplier,
                String topLabelMinutes,
                Integer bottomPositionMinutesMultiplier,
                String bottomLabelMinutes
        );

        AddStep withExpectedValues(
                Integer topPositionSecondsMultiplier,
                String topLabelSeconds,
                Integer middlePositionSecondsMultiplier,
                String middleLabelSeconds,
                Integer bottomPositionSecondsMultiplier,
                String bottomLabelSeconds,
                Integer topPositionMinutesMultiplier,
                String topLabelMinutes,
                Integer middlePositionMinutesMultiplier,
                String middleLabelMinutes,
                Integer bottomPositionMinutesMultiplier,
                String bottomLabelMinutes,
                Integer topPositionHoursMultiplier,
                String topLabelHours,
                Integer middlePositionHoursMultiplier,
                String middleLabelHours,
                Integer bottomPositionHoursMultiplier,
                String bottomLabelHours
        );
    }

    public static interface Close {
        Sequence create();
    }

    public static class Step {
        public ColumnType columnType;
        public VerticalDirection direction;
        public Integer scrollsNumber;

        Step (ColumnType columnType, VerticalDirection direction, Integer scrollsNumber) {
            this.columnType = columnType;
            this.direction = direction;
            this.scrollsNumber = scrollsNumber;
        }
    }

    public static class ExpectedValues {
        public String topLabelSeconds;
        public String middleLabelSeconds;
        public String bottomLabelSeconds;
        public Integer topPositionSecondsMultiplier;
        public Integer middlePositionSecondsMultiplier;
        public Integer bottomPositionSecondsMultiplier;

        public String topLabelMinutes;
        public String middleLabelMinutes;
        public String bottomLabelMinutes;
        public Integer topPositionMinutesMultiplier;
        public Integer middlePositionMinutesMultiplier;
        public Integer bottomPositionMinutesMultiplier;

        public String topLabelHours;
        public String middleLabelHours;
        public String bottomLabelHours;
        public Integer topPositionHoursMultiplier;
        public Integer middlePositionHoursMultiplier;
        public Integer bottomPositionHoursMultiplier;

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

        ExpectedValues (
                Integer topPositionSecondsMultiplier,
                String topLabelSeconds,
                Integer middlePositionSecondsMultiplier,
                String middleLabelSeconds,
                Integer bottomPositionSecondsMultiplier,
                String bottomLabelSeconds,
                Integer topPositionMinutesMultiplier,
                String topLabelMinutes,
                Integer middlePositionMinutesMultiplier,
                String middleLabelMinutes,
                Integer bottomPositionMinutesMultiplier,
                String bottomLabelMinutes,
                Integer topPositionHoursMultiplier,
                String topLabelHours,
                Integer middlePositionHoursMultiplier,
                String middleLabelHours,
                Integer bottomPositionHoursMultiplier,
                String bottomLabelHours
        ) {
            this.bottomLabelSeconds = bottomLabelSeconds;
            this.middleLabelSeconds = middleLabelSeconds;
            this.topLabelSeconds = topLabelSeconds;
            this.topPositionSecondsMultiplier = topPositionSecondsMultiplier;
            this.middlePositionSecondsMultiplier = middlePositionSecondsMultiplier;
            this.bottomPositionSecondsMultiplier = bottomPositionSecondsMultiplier;

            this.bottomLabelMinutes = bottomLabelMinutes;
            this.middleLabelMinutes = middleLabelMinutes;
            this.topLabelMinutes = topLabelMinutes;
            this.topPositionMinutesMultiplier = topPositionMinutesMultiplier;
            this.middlePositionMinutesMultiplier = middlePositionMinutesMultiplier;
            this.bottomPositionMinutesMultiplier = bottomPositionMinutesMultiplier;

            this.bottomLabelHours = bottomLabelHours;
            this.middleLabelHours = middleLabelHours;
            this.topLabelHours = topLabelHours;
            this.topPositionHoursMultiplier = topPositionHoursMultiplier;
            this.middlePositionHoursMultiplier = middlePositionHoursMultiplier;
            this.bottomPositionHoursMultiplier = bottomPositionHoursMultiplier;
        }
    }

    private static class SequenceBuilder implements AddStep, AddExpectedValues, AddClockStart, Close {

        final List<Step> steps = new ArrayList<>();
        private ExpectedValues expectedValues;
        private LocalTime startClock;

        @Override
        public AddStep addStep(ColumnType columnType, VerticalDirection direction, Integer scrollsNumber) {
            Step step = new Step(columnType, direction, scrollsNumber);
            steps.add(step);
            return this;
        }

        @Override
        public AddStep withExpectedValues(
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
        public AddStep withExpectedValues(
                Integer topPositionSecondsMultiplier,
                String topLabelSeconds,
                Integer middlePositionSecondsMultiplier,
                String middleLabelSeconds,
                Integer bottomPositionSecondsMultiplier,
                String bottomLabelSeconds,
                Integer topPositionMinutesMultiplier,
                String topLabelMinutes,
                Integer middlePositionMinutesMultiplier,
                String middleLabelMinutes,
                Integer bottomPositionMinutesMultiplier,
                String bottomLabelMinutes,
                Integer topPositionHoursMultiplier,
                String topLabelHours,
                Integer middlePositionHoursMultiplier,
                String middleLabelHours,
                Integer bottomPositionHoursMultiplier,
                String bottomLabelHours
        ) {
            this.expectedValues = new ExpectedValues(
                    topPositionSecondsMultiplier,
                    topLabelSeconds,
                    middlePositionSecondsMultiplier,
                    middleLabelSeconds,
                    bottomPositionSecondsMultiplier,
                    bottomLabelSeconds,
                    topPositionMinutesMultiplier,
                    topLabelMinutes,
                    middlePositionMinutesMultiplier,
                    middleLabelMinutes,
                    bottomPositionMinutesMultiplier,
                    bottomLabelMinutes,
                    topPositionHoursMultiplier,
                    topLabelHours,
                    middlePositionHoursMultiplier,
                    middleLabelHours,
                    bottomPositionHoursMultiplier,
                    bottomLabelHours
            );

            return this;
        }

        @Override
        public AddExpectedValues withStartClock(LocalTime clock) {
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
