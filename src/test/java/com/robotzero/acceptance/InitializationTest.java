package com.robotzero.acceptance;

import static org.testfx.api.FxAssert.assertContext;

import com.robotzero.counter.domain.clock.TimerRepository;
import java.time.LocalTime;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Init;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
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
    repository.create("start", LocalTime.of(0, 0, 0));
  }

  @Test
  public void it_initializes_clock_to_correct_values_based_on_provided_time(FxRobot fxRobot) {
    String[] expectedLabels = { "02", "01", "00", "59" };
    StackPane seconds = assertContext().getNodeFinder().lookup("#seconds").query();
    StackPane minutes = assertContext().getNodeFinder().lookup("#minutes").query();

    List<Node> secondsLabels = this.getNodeFinder().getLabels(seconds).get();
    List<Node> minutesLabels = this.getNodeFinder().getLabels(minutes).get();
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

  @Test
  public void it_initializes_clock_to_zero_values_when_history_is_empty(FxRobot fxRobot) {
    repository.deleteAll();
    StackPane seconds = assertContext().getNodeFinder().lookup("#seconds").query();
    StackPane minutes = assertContext().getNodeFinder().lookup("#minutes").query();

    List<Node> secondsLabels = this.getNodeFinder().getLabels(seconds).get();
    List<Node> minutesLabels = this.getNodeFinder().getLabels(minutes).get();

    Assertions.assertNull(repository.selectLatest());

    String[] expectedLabels = { "02", "01", "00", "59" };
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

  public Stream<? extends Arguments> provideArguments() {
    String[] expectedLabels = { "02", "01", "00", "59" };
    return Stream.of(Arguments.of(LocalTime.of(0, 0, 0), expectedLabels));
  }
}
