package com.robotzero.acceptance;

import static org.awaitility.Awaitility.await;
import static org.testfx.api.FxAssert.assertContext;

import com.robotzero.counter.domain.clock.TimerRepository;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Init;
import org.testfx.framework.junit5.Start;

public final class InitializationTest extends ClockFxTest {
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
    repository = this.getBean(TimerRepository.class);
    repository.deleteAll();
  }

  @ParameterizedTest
  @MethodSource("provideArguments")
  public void it_initializes_clock_to_correct_values_based_on_provided_time(
    List<String> expectedSecondsLabels,
    List<String> expectedMinutesLabels,
    List<String> expectedHoursLabels,
    LocalTime localTime,
    FxRobot fxRobot
  ) {
    this.repository.deleteAll();
    this.repository.create("latest", localTime);
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
    StackPane hours = assertContext().getNodeFinder().lookup("#hours").query();

    List<Node> secondsLabels = this.getNodeFinder().getLabels(seconds).get();
    List<Node> minutesLabels = this.getNodeFinder().getLabels(minutes).get();
    List<Node> hoursLabels = this.getNodeFinder().getLabels(hours).get();

    List<String> visibleSecondsLabels = secondsLabels
      .stream()
      .map(text -> ((Text) text).getText())
      .collect(Collectors.toList());

    for (int i = 0; i < visibleSecondsLabels.size(); i++) {
      Assertions.assertEquals(expectedSecondsLabels.get(i), visibleSecondsLabels.get(i));
    }

    List<String> visibleMinutesLabels = minutesLabels
      .stream()
      .map(text -> ((Text) text).getText())
      .collect(Collectors.toList());

    for (int i = 0; i < visibleMinutesLabels.size(); i++) {
      Assertions.assertEquals(expectedMinutesLabels.get(i), visibleMinutesLabels.get(i));
    }

    List<String> visibleHoursLabels = hoursLabels
        .stream()
        .map(text -> ((Text) text).getText())
        .collect(Collectors.toList());

    for (int i = 0; i < visibleHoursLabels.size(); i++) {
      Assertions.assertEquals(expectedHoursLabels.get(i), visibleHoursLabels.get(i));
    }
  }

  @Test
  public void it_initializes_clock_to_zero_values_when_history_is_empty(FxRobot fxRobot) {
    String[] expectedLabels = { "02", "01", "00", "59" };
    repository.deleteAll();
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

    List<Node> secondsLabels = this.getNodeFinder().getLabels(seconds).get();
    List<Node> minutesLabels = this.getNodeFinder().getLabels(minutes).get();

    Assertions.assertEquals(repository.selectLatest().getSavedTimer().toString(), "00:00");

    List<String> visibleSecondsLabels = secondsLabels
      .stream()
      .map(text -> ((Text) text).getText())
      .collect(Collectors.toList());

    for (int i = 0; i < visibleSecondsLabels.size(); i++) {
      Assertions.assertEquals(expectedLabels[i], visibleSecondsLabels.get(i));
    }

    List<String> visibleMinutesLabels = minutesLabels
      .stream()
      .map(text -> ((Text) text).getText())
      .collect(Collectors.toList());

    for (int i = 0; i < visibleMinutesLabels.size(); i++) {
      Assertions.assertEquals(expectedLabels[i], visibleMinutesLabels.get(i));
    }
  }

  private static Stream<? extends Arguments> provideArguments() {
    return Stream.of(
      Arguments.of(List.of("02", "01", "00", "59"), List.of("02", "01", "00", "59"), List.of("02", "01", "00", "23"), LocalTime.of(0, 0, 0)),
      Arguments.of(List.of("12", "11", "10", "09"), List.of("02", "01", "00", "59"), List.of("14", "13", "12", "11"), LocalTime.of(12, 0, 10)),
      Arguments.of(List.of("01", "00", "59", "58"), List.of("16", "15", "14", "13"), List.of("02", "01", "00", "23"), LocalTime.of(0, 14, 59))
    );
  }
}
