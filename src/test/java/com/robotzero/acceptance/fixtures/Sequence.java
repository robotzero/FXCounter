package com.robotzero.acceptance.fixtures;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static org.testfx.api.FxAssert.assertContext;

import com.google.code.tempusfugit.temporal.WaitFor;
import com.robotzero.acceptance.ClockFxTest;
import com.robotzero.counter.domain.ColumnType;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import javafx.geometry.VerticalDirection;
import javafx.scene.control.Button;
import org.testfx.api.FxRobot;

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
    AddStep addScroll(ColumnType columnType, VerticalDirection direction, Integer scrollsNumber);
    AddStep addReset();
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

  public static interface Step {
    public void execute(FxRobot fxRobot);
  }

  public static class ScrollStep implements Step {
    public ColumnType columnType;
    public VerticalDirection direction;
    public Integer scrollsNumber;

    ScrollStep(ColumnType columnType, VerticalDirection direction, Integer scrollsNumber) {
      this.columnType = columnType;
      this.direction = direction;
      this.scrollsNumber = scrollsNumber;
    }

    @Override
    public void execute(FxRobot fxrobot) {
      String paneName = this.columnType.name().toLowerCase();
      fxrobot.moveTo("#" + paneName);
      IntStream
        .range(0, this.scrollsNumber)
        .forEach(
          i -> {
            fxrobot.scroll(this.direction);
            try {
              WaitFor.waitUntil(timeout(millis(ClockFxTest.TIME_WAIT)));
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        );
    }
  }

  public static class ResetStep implements Step {

    @Override
    public void execute(FxRobot fxrobot) {
      fxrobot.clickOn((Button) assertContext().getNodeFinder().lookup("#reset").query());
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

    ExpectedValues(
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

    ExpectedValues(
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
    public AddStep addScroll(ColumnType columnType, VerticalDirection direction, Integer scrollsNumber) {
      Step step = new ScrollStep(columnType, direction, scrollsNumber);
      steps.add(step);
      return this;
    }

    @Override
    public AddStep addReset() {
      Step step = new ResetStep();
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
      this.expectedValues =
        new ExpectedValues(
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
      this.expectedValues =
        new ExpectedValues(
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
