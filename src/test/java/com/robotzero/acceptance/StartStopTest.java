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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Init;
import org.testfx.framework.junit5.Start;

public class StartStopTest extends ClockFxTest {
  private Button startButton, resetButton;
  private TimerRepository repository;

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
    startButton = assertContext().getNodeFinder().lookup("#start").query();
    resetButton = assertContext().getNodeFinder().lookup("#reset").query();

    repository = this.getBean(TimerRepository.class);
    repository.deleteAll();
  }

  @Test
  public void start_button_starts_animation(FxRobot fxRobot) {
    repository.create("latest", LocalTime.of(0, 10, 3));
    restart();
    await()
      .atMost(Duration.of(120, ChronoUnit.SECONDS))
      .until(
        () -> {
          return stage.isShowing() && stage.getTitle().equals("Count Me Bubbles!");
        }
      );

    fxRobot.clickOn(startButton);

    try {
      WaitFor.waitUntil(timeout(millis(4000)));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    verifyThat(
      "#seconds",
      (StackPane seconds) -> {
        List<Node> secondsLabels = this.getNodeFinder().getLabels(seconds).get();
        boolean present = secondsLabels.stream().anyMatch(text -> ((Text) text).getText().equals("04"));
        return !present;
      }
    );

    verifyThat(
      "#minutes",
      (StackPane minutes) -> {
        List<Node> minutesLabels = this.getNodeFinder().getLabels(minutes).get();
        boolean present = minutesLabels.stream().anyMatch(text -> ((Text) text).getText().equals("12"));
        return !present;
      }
    );
  }

  @Test
  public void while_animation_is_running_stop_and_reset_buttons_are_disabled(FxRobot fxRobot) {
    repository.create("latest", LocalTime.of(0, 10, 3));
    restart();
    await()
      .atMost(Duration.of(120, ChronoUnit.SECONDS))
      .until(
        () -> {
          return stage.isShowing() && stage.getTitle().equals("Count Me Bubbles!");
        }
      );

    Assertions.assertFalse(resetButton.disableProperty().get());
    Assertions.assertEquals(startButton.getText(), "Start");

    fxRobot.clickOn(startButton);

    try {
      WaitFor.waitUntil(timeout(millis(1000)));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    boolean isResetDisabled = resetButton.disableProperty().get();
    String startButtonText = startButton.getText();

    Assertions.assertTrue(isResetDisabled);
    Assertions.assertEquals(startButtonText, "Pause");

    fxRobot.clickOn(startButton);
  }

  @Test
  public void disallow_start_when_clock_already_at_zero(FxRobot fxRobot) {
    // Prepare clock state;
    repository.create("start", LocalTime.of(0, 0, 0));
    restart();
    await()
      .atMost(Duration.of(120, ChronoUnit.SECONDS))
      .until(
        () -> {
          return stage.isShowing() && stage.getTitle().equals("Count Me Bubbles!");
        }
      );

    Button start = assertContext().getNodeFinder().lookup("#start").query();

    Assertions.assertTrue(start.disableProperty().get());
  }

  @Test
  public void start_button_changes_to_pause_after_clicking_start_and_back_to_start(FxRobot fxRobot) {
    // Prepare clock state;
    repository.create("start", LocalTime.of(2, 2, 0));
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

    fxRobot.clickOn(reset);
    fxRobot.clickOn(start);

    verifyThat("#start", (Button b) -> b.textProperty().get().equals("Pause"));

    fxRobot.clickOn(start);

    verifyThat("#start", (Button b) -> b.textProperty().get().equals("Start"));
  }
}
