package com.robotzero.acceptance;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static org.awaitility.Awaitility.await;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;

import com.google.code.tempusfugit.temporal.WaitFor;
import com.robotzero.counter.domain.clock.TimerRepository;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Init;
import org.testfx.framework.junit5.Start;

public class TickingTest extends ClockFxTest {
  private TimerRepository repository;

  public static Stream<? extends Arguments> ticks() {
    // time, time wait, multi sec top, label top, multi sec bottom, label bottom... minutes.
    return Stream.of(
      Arguments.of(LocalTime.of(12, 10, 1), 1300, 3, "59", 2, "00", 1, "11", 3, "09")
      //        Arguments.of(LocalTime.of(12, 10, 0), 900, 0, "01", 2, "59", 0, "11", 2, "09"),
      //        Arguments.of(LocalTime.of(12, 10, 59), 900, 0, "00", 2, "58", 1, "11", 3, "09"),
      //        Arguments.of(LocalTime.of(12, 10, 3), 3600, 1, "00", 3, "58", 0, "11", 2, "09")
    );
  }

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
  }

  @ParameterizedTest
  @MethodSource("ticks")
  public void ticking(
    LocalTime localTime,
    Integer waitTime,
    Integer mutli1,
    String lbl1,
    Integer multi2,
    String lbl2,
    Integer multi3,
    String lbl3,
    Integer multi4,
    String lbl4,
    FxRobot fxRobot
  ) {
    // Prepare clock state;
    repository.create("start", localTime);

    restart();
    await()
      .atMost(Duration.of(120, ChronoUnit.SECONDS))
      .until(
        () -> {
          return stage.isShowing() && stage.getTitle().equals("Count Me Bubbles!");
        }
      );

    Button reset = assertContext().getNodeFinder().lookup("#reset").query();
    Button start = assertContext().getNodeFinder().lookup("#start").query();
    StackPane seconds = assertContext().getNodeFinder().lookup("#seconds").query();
    StackPane minutes = assertContext().getNodeFinder().lookup("#minutes").query();

    // Grab the bottom list of rectangles and labels
    List<Node> secondsRectangles = this.getNodeFinder().getRectangles(seconds).get();
    List<Node> secondsLabels = this.getNodeFinder().getLabels(seconds).get();
    List<Node> minutesRectangles = this.getNodeFinder().getRectangles(minutes).get();
    List<Node> minutesLabels = this.getNodeFinder().getLabels(minutes).get();

    // Current height
    int height = (int) ((VBox) minutesRectangles.get(0)).getHeight() + 1;

    String topIdSeconds = secondsRectangles.stream().filter(r -> r.getTranslateY() == -90).findAny().get().getId();
    String bottomIdSeconds = secondsRectangles
      .stream()
      .filter(r -> r.getTranslateY() == height * 2)
      .findAny()
      .get()
      .getId();

    String topIdMinutes = minutesRectangles.stream().filter(r -> r.getTranslateY() == -90).findAny().get().getId();
    String bottomIdMinutes = minutesRectangles
      .stream()
      .filter(r -> r.getTranslateY() == height * 2)
      .findAny()
      .get()
      .getId();

    fxRobot.clickOn(start);

    try {
      WaitFor.waitUntil(timeout(millis(waitTime)));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

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
          String labelTop = secondsRectangles
            .stream()
            .filter(tr -> tr.getId().equals(exTopRectangle.get().getId()))
            .map(tt -> ((Text) ((VBox) tt).getChildrenUnmodifiable().get(0)).getText())
            .findAny()
            .get();
          String labelBottom = secondsRectangles
            .stream()
            .filter(tr -> tr.getId().equals(bottomIdSeconds))
            .map(tt -> ((Text) ((VBox) tt).getChildrenUnmodifiable().get(0)).getText())
            .findAny()
            .get();

          return (
            exTopRectangle.get().getParent().getParent().getTranslateY() == mutli1 * height &&
            labelTop.equals(lbl1) &&
            exBottomRectangle.get().getParent().getParent().getTranslateY() == multi2 * height &&
            labelBottom.equals(lbl2)
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
            .filter(tr -> tr.getId().equals(exTopRectangle.get().getId()))
            .map(tt -> ((Text) tt).getText())
            .findAny()
            .get();
          String labelBottom = minutesLabels
            .stream()
            .filter(tr -> tr.getId().equals(bottomIdMinutes))
            .map(tt -> ((Text) tt).getText())
            .findAny()
            .get();

          return (
            exTopRectangle.get().getParent().getParent().getTranslateY() == multi3 * height &&
            labelTop.equals(lbl3) &&
            exBottomRectangle.get().getParent().getParent().getTranslateY() == multi4 * height &&
            labelBottom.equals(lbl4)
          );
        }

        return false;
      }
    );
  }

  @Test
  public void stops_ticking_when_reached_zero(FxRobot fxRobot) {
    restart();
    await()
      .atMost(Duration.of(120, ChronoUnit.SECONDS))
      .until(
        () -> {
          return stage.isShowing() && stage.getTitle().equals("Count Me Bubbles!");
        }
      );
    // Prepare clock state;
    repository.create("start", LocalTime.of(0, 0, 4));

    Button reset = assertContext().getNodeFinder().lookup("#reset").query();
    Button start = assertContext().getNodeFinder().lookup("#start").query();
    StackPane seconds = assertContext().getNodeFinder().lookup("#paneSeconds").query();
    StackPane minutes = assertContext().getNodeFinder().lookup("#paneMinutes").query();

    // Grab the bottom list of rectangles and labels
    List<Node> secondsRectangles = this.getNodeFinder().getRectangles(seconds).get();
    List<Node> secondsLabels = this.getNodeFinder().getLabels(seconds).get();
    List<Node> minutesRectangles = this.getNodeFinder().getRectangles(minutes).get();
    List<Node> minutesLabels = this.getNodeFinder().getLabels(minutes).get();

    // Current height
    int height = (int) ((Rectangle) minutesRectangles.get(0)).getHeight() + 1;

    String topIdSeconds = secondsRectangles
      .stream()
      .filter(r -> r.getParent().getParent().getTranslateY() == height)
      .findAny()
      .get()
      .getId();
    String bottomIdSeconds = secondsRectangles
      .stream()
      .filter(r -> r.getParent().getParent().getTranslateY() == height * 3)
      .findAny()
      .get()
      .getId();

    String topIdMinutes = minutesRectangles
      .stream()
      .filter(r -> r.getParent().getParent().getTranslateY() == height)
      .findAny()
      .get()
      .getId();
    String bottomIdMinutes = minutesRectangles
      .stream()
      .filter(r -> r.getParent().getParent().getTranslateY() == height * 3)
      .findAny()
      .get()
      .getId();

    fxRobot.clickOn(reset);
    fxRobot.clickOn(start);

    try {
      WaitFor.waitUntil(timeout(millis(TIME_WAIT * 4 * 2)));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

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
            .filter(tr -> tr.getId().equals(exTopRectangle.get().getId()))
            .map(tt -> ((Text) tt).getText())
            .findAny()
            .get();
          String labelBottom = secondsLabels
            .stream()
            .filter(tr -> tr.getId().equals(bottomIdSeconds))
            .map(tt -> ((Text) tt).getText())
            .findAny()
            .get();

          return (
            exTopRectangle.get().getParent().getParent().getTranslateY() == height &&
            labelTop.equals("01") &&
            exBottomRectangle.get().getParent().getParent().getTranslateY() == 3 * height &&
            labelBottom.equals("59")
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
            .filter(tr -> tr.getId().equals(exTopRectangle.get().getId()))
            .map(tt -> ((Text) tt).getText())
            .findAny()
            .get();
          String labelBottom = minutesLabels
            .stream()
            .filter(tr -> tr.getId().equals(bottomIdMinutes))
            .map(tt -> ((Text) tt).getText())
            .findAny()
            .get();

          return (
            exTopRectangle.get().getParent().getParent().getTranslateY() == height &&
            labelTop.equals("01") &&
            exBottomRectangle.get().getParent().getParent().getTranslateY() == 3 * height &&
            labelBottom.equals("59")
          );
        }

        return false;
      }
    );
  }
}
