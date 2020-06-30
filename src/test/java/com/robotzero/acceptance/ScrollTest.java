package com.robotzero.acceptance;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static org.awaitility.Awaitility.await;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;

import com.google.code.tempusfugit.temporal.WaitFor;
import com.robotzero.acceptance.fixtures.Sequence;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.clock.TimerRepository;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Init;
import org.testfx.framework.junit5.Start;

public class ScrollTest extends ClockFxTest {
  private TimerRepository repository;
  private BooleanProperty resetOption;

  @Init
  public void init() {
    super.init();
  }

  @Start
  public void start(Stage stage) {
    super.start(stage);
  }

  @BeforeEach
  public void setUp() {
    repository = this.getBean(TimerRepository.class);
    repository.deleteAll();
    repository.create("start", LocalTime.of(0, 0, 0));
    resetOption = new SimpleBooleanProperty(false);
  }

  public static Stream<? extends Arguments> scrolls() {
    return Stream.of(
      Arguments.of(
        // @formatter:off
                // Seconds UP
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(0, "14", 3, "11", -1, "18", 2, "15")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1).close())
      ),
      Arguments.of(
                            com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                    .withStartClock(DEFAULT_CLOCK_STATE)
                                    .withExpectedValues(1, "14", 0, "15", -1, "18", 2, "15")
                                    .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2).close())),
      Arguments.of(

                            com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                    .withStartClock(DEFAULT_CLOCK_STATE)
                                    .withExpectedValues(2, "14", 1, "15", -1, "18", 2, "15")
                                    .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 3).close())),
      Arguments.of(
                            com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                    .withStartClock(DEFAULT_CLOCK_STATE)
                                    .withExpectedValues(3, "14", 2, "15", -1, "18", 2, "15")
                                    .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 4).close())),
      Arguments.of(
                            com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                    .withStartClock(DEFAULT_CLOCK_STATE)
                                    .withExpectedValues(0, "18", 3, "15", -1, "18", 2, "15")
                                    .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 5).close())),
      Arguments.of(
                            com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                    .withStartClock(DEFAULT_CLOCK_STATE)
                                    .withExpectedValues(1, "18", 0, "19", -1, "18", 2, "15")
                                    .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 6).close())
        //                ,
        //                // Seconds DOWN
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(3, "10", 2, "11", 0, "18", 3, "15")
        //                            .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 1).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(2, "10", 1, "11", 0, "18", 3, "15")
        //                            .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 2).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(1, "10", 0, "11", 0, "18", 3, "15")
        //                            .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 3).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(0, "10", 3, "07", 0, "18", 3, "15")
        //                            .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 4).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(3, "06", 2, "07", 0, "18", 3, "15")
        //                            .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 5).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(2, "06", 1, "07", 0, "18", 3, "15")
        //                            .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 6).close())
        //                ,
        //                // Minutes UP
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(0, "14", 3, "11", 1, "18", 4, "15")
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(0, "14", 3, "11", 2, "18", 1, "19")
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 2).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(0, "14", 3, "11", 3, "18", 2, "19")
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 3).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(0, "14", 3, "11", 4, "18", 3, "19")
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 4).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(0, "14", 3, "11", 1, "22", 4, "19")
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 5).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(0, "14", 3, "11", 2, "22", 1, "23")
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 6).close())
        //                ,
        //                // Minutes DOWN
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(0, "14", 3, "11", 3, "14", 2, "15")
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 1).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(0, "14", 3, "11", 2, "14", 1, "15")
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 2).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(0, "14", 3, "11", 1, "14", 0, "15")
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 3).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(0, "14", 3, "11", 0, "14", 3, "11")
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 4).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(0, "14", 3, "11", 3, "10", 2, "11")
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 5).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(0, "14", 3, "11", 2, "10", 1, "11")
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 6).close())
        //                ,
        //                // Seconds and Minutes
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(2, "10", 1, "11", 3, "18", 2, "19")
        //                            .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 2)
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 3).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                            .withStartClock(DEFAULT_CLOCK_STATE)
        //                            .withExpectedValues(4, "14", 3, "15", 1, "14", 4, "11")
        //                            .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 3)
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 4)
        //                            .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
        //                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1).close())
        //                ,
        //
        //                   com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                           .withStartClock(DEFAULT_CLOCK_STATE)
        //                           .withExpectedValues(0, "14", 3, "11", 0, "18", 3, "15")
        //                           .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
        //                           .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1)
        //                           .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 1)
        //                           .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 1).close())
        //                ,
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                          .withStartClock(DEFAULT_CLOCK_STATE)
        //                          .withExpectedValues(0, "14", 3, "11", 2, "18", 1, "19")
        //                          .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
        //                          .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1)
        //                          .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 1)
        //                          .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 1)
        //                          .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 2).close())
        //                ,
        //                // Different clock start state
        //
        //                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                           .withStartClock(LocalTime.of(0, 0, 0))
        //                           .withExpectedValues(3, "02", 2, "03", 1, "58", 0, "59")
        //                           .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2)
        //                           .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 3)
        //                           .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1).close())
        //                ,
        //
        //                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                                .withStartClock(LocalTime.of(0, 0, 0))
        //                                .withExpectedValues(2, "02", 1, "03", 3, "58", 2, "59")
        //                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
        //                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 1)
        //                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1).close())
        //                ,
        //
        //                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
        //                                .withStartClock(LocalTime.of(0, 59, 56))
        //                                .withExpectedValues(1, "58", 0, "59", 3, "57", 2, "58")
        //                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2)
        //                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 1)
        //                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 1).close())
      )
    );
    //@formatter:on
  }

  @ParameterizedTest
  @MethodSource("scrolls")
  public void scroll(Sequence sequence, FxRobot fxRobot) {
    // Should we check the database for new clock data.
    resetOption.setValue(true);
    // Prepare clock state;
    repository.create("start", sequence.clockStartState);
    restart();
    await()
      .atMost(Duration.of(5, ChronoUnit.SECONDS))
      .until(
        () -> {
          return stage.isShowing() && stage.getTitle().equals("Count Me Bubbles!");
        }
      );

    StackPane seconds = assertContext().getNodeFinder().lookup("#seconds").query();
    StackPane minutes = assertContext().getNodeFinder().lookup("#minutes").query();

    // Grab the bottom list of rectangles and labels
    List<Node> secondsRectangles = getNodeFinder().getRectangles(seconds).get();
    List<Node> minutesRectangles = getNodeFinder().getRectangles(minutes).get();
    List<Node> secondsLabels = getNodeFinder().getLabels(seconds).get();
    List<Node> minutesLabels = getNodeFinder().getLabels(minutes).get();

    // Current height
    int height = (int) ((VBox) secondsRectangles.get(0)).getHeight() + 1;

    String topIdSeconds = secondsRectangles
      .stream()
      .filter(r -> r.getTranslateY() == TOP_NODE_LOCATION)
      .findAny()
      .get()
      .getId();
    String bottomIdSeconds = secondsRectangles
      .stream()
      .filter(r -> r.getTranslateY() == height * 2)
      .findAny()
      .get()
      .getId();
    String topIdMinutes = minutesRectangles
      .stream()
      .filter(r -> r.getTranslateY() == TOP_NODE_LOCATION)
      .findAny()
      .get()
      .getId();
    String bottomIdMinutes = minutesRectangles
      .stream()
      .filter(r -> r.getTranslateY() == height * 2)
      .findAny()
      .get()
      .getId();

    sequence.steps.forEach(
      step -> {
        step.execute(fxRobot);
      }
    );

    verifyThat(
      "#seconds",
      (StackPane s) -> {
        Optional<Node> exTopRectangle = secondsRectangles
          .stream()
          .filter(rt -> rt.getId().equals(topIdSeconds))
          .findAny();
        Optional<Node> exBottomRectangle = secondsRectangles
          .stream()
          .filter(rt -> rt.getId().equals(bottomIdSeconds))
          .findAny();
        if (exTopRectangle.isPresent() && exBottomRectangle.isPresent()) {
          String labelTop = secondsLabels
            .stream()
            .filter(tr -> tr.getParent().getId().equals(exTopRectangle.get().getId()))
            .map(tt -> ((Text) tt).getText())
            .findAny()
            .get();
          String labelBottom = secondsLabels
            .stream()
            .filter(tr -> tr.getParent().getId().equals(bottomIdSeconds))
            .map(tt -> ((Text) tt).getText())
            .findAny()
            .get();

          return (
            exTopRectangle.get().getTranslateY() == sequence.expectedValues.topPositionSecondsMultiplier * height &&
            labelTop.equals(sequence.expectedValues.topLabelSeconds) &&
            exBottomRectangle.get().getTranslateY() ==
            sequence.expectedValues.bottomPositionSecondsMultiplier *
            height &&
            labelBottom.equals(sequence.expectedValues.bottomLabelSeconds)
          );
        }

        return false;
      }
    );

    verifyThat(
      "#minutes",
      (StackPane s) -> {
        Optional<Node> exTopRectangle = minutesRectangles
          .stream()
          .filter(rt -> rt.getId().equals(topIdMinutes))
          .findAny();
        Optional<Node> exBottomRectangle = minutesRectangles
          .stream()
          .filter(rt -> rt.getId().equals(bottomIdMinutes))
          .findAny();
        if (exTopRectangle.isPresent() && exBottomRectangle.isPresent()) {
          String labelTop = minutesLabels
            .stream()
            .filter(tr -> tr.getParent().getId().equals(exTopRectangle.get().getId()))
            .map(tt -> ((Text) tt).getText())
            .findAny()
            .get();
          String labelBottom = minutesLabels
            .stream()
            .filter(tr -> tr.getParent().getId().equals(bottomIdMinutes))
            .map(tt -> ((Text) tt).getText())
            .findAny()
            .get();

          return (
            exTopRectangle.get().getTranslateY() == sequence.expectedValues.topPositionMinutesMultiplier * height &&
            labelTop.equals(sequence.expectedValues.topLabelMinutes) &&
            exBottomRectangle.get().getTranslateY() ==
            sequence.expectedValues.bottomPositionMinutesMultiplier *
            height &&
            labelBottom.equals(sequence.expectedValues.bottomLabelMinutes)
          );
        }

        return false;
      }
    );
  }

  @Test
  public void unable_to_scroll_after_pressing_start(FxRobot fxRobot) {
    // Should we check the database for new clock data.
    resetOption.setValue(true);
    // Prepare clock state;
    repository.create("start", DEFAULT_CLOCK_STATE);

    Button reset = assertContext().getNodeFinder().lookup("#reset").query();
    Button start = assertContext().getNodeFinder().lookup("#start").query();
    StackPane minutes = assertContext().getNodeFinder().lookup("#minutes").query();

    // Grab the bottom list of rectangles and labels
    List<Node> minutesRectangles = getNodeFinder().getRectangles(minutes).get();
    List<Node> minutesLabels = getNodeFinder().getLabels(minutes).get();

    // Current height
    int height = (int) minutesRectangles.get(0).getBoundsInParent().getHeight() + 1;

    String topIdMinutes = minutesRectangles.stream().filter(r -> r.getTranslateY() == -90).findAny().get().getId();

    String bottomIdMinutes = minutesRectangles
      .stream()
      .filter(r -> r.getTranslateY() == height * 2)
      .findAny()
      .get()
      .getId();

    fxRobot.clickOn(reset);
    fxRobot.clickOn(start);

    fxRobot.moveTo("#minutes");

    IntStream
      .range(0, 3)
      .forEach(
        i -> {
          fxRobot.scroll(VerticalDirection.DOWN);
          try {
            WaitFor.waitUntil(timeout(millis(ClockFxTest.TIME_WAIT)));
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      );

    FxAssert.verifyThat(
      "#minutes",
      (StackPane s) -> {
        Optional<Node> exTopRectangle = minutesRectangles
          .stream()
          .filter(rt -> rt.getId().equals(topIdMinutes))
          .findAny();
        Optional<Node> exBottomRectangle = minutesRectangles
          .stream()
          .filter(rt -> rt.getId().equals(bottomIdMinutes))
          .findAny();
        if (exTopRectangle.isPresent() && exBottomRectangle.isPresent()) {
          String labelTop = getNodeFinder()
            .getRectangles(minutes)
            .get()
            .stream()
            .filter(tr -> tr.getId().equals(exTopRectangle.get().getId()))
            .map(tt -> ((Text) ((VBox) tt).getChildrenUnmodifiable().get(0)).getText())
            .findAny()
            .get();
          String labelBottom = getNodeFinder()
            .getRectangles(minutes)
            .get()
            .stream()
            .filter(tr -> tr.getId().equals(bottomIdMinutes))
            .map(tt -> ((Text) ((VBox) tt).getChildrenUnmodifiable().get(0)).getText())
            .findAny()
            .get();

          return (
            exTopRectangle.get().getTranslateY() == 0 &&
            labelTop.equals("18") &&
            exBottomRectangle.get().getTranslateY() == 2 * height &&
            labelBottom.equals("15")
          );
        }
        return false;
      }
    );
  }
}
