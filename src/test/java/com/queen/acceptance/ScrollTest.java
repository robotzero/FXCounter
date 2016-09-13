package com.queen.acceptance;

import com.google.code.tempusfugit.temporal.WaitFor;
import com.queen.counter.domain.ColumnType;
import com.queen.counter.repository.SavedTimerRepository;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static com.queen.acceptance.ScrollTest.ExpectedValues.expected;
import static com.queen.acceptance.ScrollTest.Sequence.sequence;
import static com.queen.acceptance.ScrollTest.Step.step;
import static java.util.Arrays.asList;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;

@RunWith(DataProviderRunner.class)
public class ScrollTest extends CounterAppIT {

    private SavedTimerRepository repository;
    private BooleanProperty resetOption;

    @Before
    public void setUp() {
        resetOption = this.getBean(BooleanProperty.class);
        repository = this.getBean(SavedTimerRepository.class);
        repository.deleteAll();
    }

    @DataProvider
    public static Object[][] scrolls() {
        return new Object[][]{
                // @formatter:off
                // Seconds UP
                {sequence(
                        expected(1, "14", 4, "11", 0, "18", 3, "14"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.UP, 1)
                )},
                {sequence(
                        expected(2, "14", 1, "15", 0, "18", 3, "14"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.UP, 2)
                )},
                {sequence(
                        expected(3, "14", 2, "15", 0, "18", 3, "14"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.UP, 3)
                )},
                {sequence(
                        expected(4, "14", 3, "15", 0, "18", 3, "14"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.UP, 4)
                )},
                {sequence(
                        expected(1, "18", 4, "15", 0, "18", 3, "14"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.UP, 5)
                )},
                {sequence(
                        expected(2, "18", 1, "19", 0, "18", 3, "14"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.UP, 6)
                )},
                // Seconds DOWN
                {sequence(
                        expected(3, "10", 2, "11", 0, "18", 3, "14"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 1)
                )},
                {sequence(
                        expected(2, "10", 1, "11", 0, "18", 3, "14"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 2)
                )},
                {sequence(
                        expected(1, "10", 0, "11", 0, "18", 3, "14"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 3)
                )},
                {sequence(
                        expected(0, "10", 3, "07", 0, "18", 3, "14"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 4)
                )},
                {sequence(
                        expected(3, "06", 2, "07", 0, "18", 3, "14"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 5)
                )},
                {sequence(
                        expected(2, "06", 1, "07", 0, "18", 3, "14"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 6)
                )},
                // Minutes UP
                {sequence(
                        expected(0, "14", 3, "11", 1, "18", 4, "15"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.MINUTES, VerticalDirection.UP, 1)
                )},
                {sequence(
                        expected(0, "14", 3, "11", 2, "18", 1, "19"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.MINUTES, VerticalDirection.UP, 2)
                )},
                {sequence(
                        expected(0, "14", 3, "11", 3, "18", 2, "19"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.MINUTES, VerticalDirection.UP, 3)
                )},
                {sequence(
                        expected(0, "14", 3, "11", 4, "18", 3, "19"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.MINUTES, VerticalDirection.UP, 4)
                )},
                {sequence(
                        expected(0, "14", 3, "11", 1, "22", 4, "19"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.MINUTES, VerticalDirection.UP, 5)
                )},
                {sequence(
                        expected(0, "14", 3, "11", 2, "22", 1, "23"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.MINUTES, VerticalDirection.UP, 6)
                )},
                // Minutes DOWN
                {sequence(
                        expected(0, "14", 3, "11", 3, "14", 2, "15"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 1)
                )},
                {sequence(
                        expected(0, "14", 3, "11", 2, "14", 1, "15"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 2)
                )},
                {sequence(
                        expected(0, "14", 3, "11", 1, "14", 0, "15"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 3)
                )},
                {sequence(
                        expected(0, "14", 3, "11", 0, "14", 3, "11"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 4)
                )},
                {sequence(
                        expected(0, "14", 3, "11", 3, "10", 2, "11"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 5)
                )},
                {sequence(
                        expected(0, "14", 3, "11", 2, "10", 1, "11"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 6)
                )},
                // Seconds and Minutes
                {sequence(
                        expected(2, "10", 1, "11", 3, "18", 2, "19"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 2),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 3)
                )},
                {sequence(
                        expected(2, "10", 1, "11", 3, "18", 2, "19"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 2),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 3)
                )},
                {sequence(
                        expected(4, "14", 3, "15", 1, "14", 4, "11"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.UP, 3),
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 4),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 1),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 1)
                )},
                {sequence(
                        expected(0, "14", 3, "11", 0, "18", 3, "15"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.UP, 1),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 1),
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 1),
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 1)
                )},
                {sequence(
                        expected(0, "14", 3, "11", 2, "18", 1, "19"),
                        DEFAULT_CLOCK_STATE,
                        step(ColumnType.SECONDS, VerticalDirection.UP, 1),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 1),
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 1),
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 1),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 2)
                )},
                // Different clock start state
                {sequence(
                        expected(3, "02", 2, "03", 1, "58", 0, "59"),
                        LocalTime.of(0, 0, 0),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 2),
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 3),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 1)
                )}
                //@formatter:on
        };
    }

    @Test
    @UseDataProvider("scrolls")
    public void scroll(Sequence sequence) {

        // Should we check the database for new clock data.
        resetOption.setValue(true);
        // Prepare clock state;
        repository.create("start", sequence.clockStartState);

        Button reset = assertContext().getNodeFinder().lookup("#reset").queryFirst();
        StackPane seconds = assertContext().getNodeFinder().lookup("#paneSeconds").queryFirst();
        StackPane minutes = assertContext().getNodeFinder().lookup("#paneMinutes").queryFirst();

        // Grab the bottom list of rectangles.
        List<Node> secondsRectangles = seconds.getChildren().stream()
                             .filter(n -> !n.getClass().equals(Rectangle.class))
                             .map(v -> ((VBox) v).getChildren())
                             .flatMap(Collection::stream)
                             .filter(s -> s.getClass().equals(StackPane.class))
                             .map(sl -> ((StackPane) sl).getChildren())
                             .flatMap(Collection::stream)
                             .filter(r -> r.getClass().equals(Rectangle.class))
                             .collect(Collectors.toList());

        List<Node> secondsLabels = seconds.getChildren().stream()
                .filter(n -> !n.getClass().equals(Rectangle.class))
                .map(v -> ((VBox) v).getChildren())
                .flatMap(Collection::stream)
                .filter(s -> s.getClass().equals(StackPane.class))
                .map(sl -> ((StackPane) sl).getChildren())
                .flatMap(Collection::stream)
                .filter(r -> r.getClass().equals(Text.class))
                .collect(Collectors.toList());

        List<Node> minutesRectangles = minutes.getChildren().stream()
                .filter(n -> !n.getClass().equals(Rectangle.class))
                .map(v -> ((VBox) v).getChildren())
                .flatMap(Collection::stream)
                .filter(s -> s.getClass().equals(StackPane.class))
                .map(sl -> ((StackPane) sl).getChildren())
                .flatMap(Collection::stream)
                .filter(r -> r.getClass().equals(Rectangle.class))
                .collect(Collectors.toList());

        List<Node> minutesLabels = minutes.getChildren().stream()
                .filter(n -> !n.getClass().equals(Rectangle.class))
                .map(v -> ((VBox) v).getChildren())
                .flatMap(Collection::stream)
                .filter(s -> s.getClass().equals(StackPane.class))
                .map(sl -> ((StackPane) sl).getChildren())
                .flatMap(Collection::stream)
                .filter(r -> r.getClass().equals(Text.class))
                .collect(Collectors.toList());

        // Current height
        int height = (int) ((Rectangle) secondsRectangles.get(0)).getHeight();

        String topIdSeconds = secondsRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == TOP_NODE_LOCATION).findAny().get().getId();
        String bottomIdSeconds = secondsRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height * 3).findAny().get().getId();
        String topIdMinutes = minutesRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == TOP_NODE_LOCATION).findAny().get().getId();
        String bottomIdMinutes = minutesRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height * 3).findAny().get().getId();

        clickOn(reset);
        sequence.steps.forEach(step -> {
            String type = step.columnType.name();
            String paneName = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
            moveTo("#pane" + paneName);
            IntStream.range(0, step.scrollsNumber).forEach(i -> {
                scroll(step.direction);
                try {
                    WaitFor.waitUntil(timeout(millis(TIME_WAIT)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });

        verifyThat("#paneSeconds", (StackPane s) -> {
            Optional<Node> exTopRectangle = secondsRectangles.stream().filter(rt -> rt.getId().equals(topIdSeconds)).findAny();
            Optional<Node> exBottomRectangle = secondsRectangles.stream().filter(rt -> rt.getId().equals(bottomIdSeconds)).findAny();
            if (exTopRectangle.isPresent()) {
                String label =  secondsLabels.stream().filter(tr -> tr.getId().equals(exTopRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                return exTopRectangle.get().getParent().getParent().getTranslateY() == sequence.expectedValues.topPositionSecondsMultiplier * height && label.equals(sequence.expectedValues.topLabelSeconds);
            }

            if (exBottomRectangle.isPresent()) {
                String label =  secondsLabels.stream().filter(tr -> tr.getId().equals(bottomIdSeconds)).map(tt -> ((Text) tt).getText()).findAny().get();
                return exBottomRectangle.get().getParent().getParent().getTranslateY() == sequence.expectedValues.bottomPositionSecondsMultiplier * height && label.equals(sequence.expectedValues.bottomLabelSeconds);
            }
            return false;
        });

        verifyThat("#paneMinutes", (StackPane s) -> {
            Optional<Node> exTopRectangle = minutesRectangles.stream().filter(rt -> rt.getId().equals(topIdMinutes)).findAny();
            Optional<Node> exBottomRectangle = minutesRectangles.stream().filter(rt -> rt.getId().equals(bottomIdMinutes)).findAny();
            if (exTopRectangle.isPresent()) {
                String label =  minutesLabels.stream().filter(tr -> tr.getId().equals(exTopRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                return exTopRectangle.get().getParent().getParent().getTranslateY() == sequence.expectedValues.topPositionMinutesMultiplier * height && label.equals(sequence.expectedValues.topLabelMinutes);
            }

            if (exBottomRectangle.isPresent()) {
                String label =  minutesLabels.stream().filter(tr -> tr.getId().equals(bottomIdMinutes)).map(tt -> ((Text) tt).getText()).findAny().get();
                return exBottomRectangle.get().getParent().getParent().getTranslateY() == sequence.expectedValues.bottomPositionMinutesMultiplier * height && label.equals(sequence.expectedValues.bottomLabelMinutes);
            }

            return false;
        });
    }

    // @TODO test ticking, including minutes are up when seconds are at 0
    // @TODO test that you can't move columns when time is ticking.

    static class Step {
        ColumnType columnType;
        VerticalDirection direction;
        Integer scrollsNumber;

        static Step step(ColumnType columnType, VerticalDirection direction, Integer scrollsNumber) {
            Step step = new Step();
            step.columnType = columnType;
            step.direction = direction;
            step.scrollsNumber = scrollsNumber;
            return step;
        }
    }

    static class Sequence {
        List<Step> steps = new ArrayList<>();
        LocalTime clockStartState;
        ExpectedValues expectedValues;

        static Sequence sequence(ExpectedValues expectedValues, LocalTime clockStartState, Step ...step) {
            Sequence sequence = new Sequence();
            sequence.steps.addAll(asList(step));
            sequence.expectedValues = expectedValues;
            sequence.clockStartState = clockStartState;
            return sequence;
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

        static ExpectedValues expected(
                Integer topPositionSecondsMultiplier,
                String topLabelSeconds,
                Integer bottomPositionSecondsMultiplier,
                String bottomLabelSeconds,
                Integer topPositionMinutesMultiplier,
                String topLabelMinutes,
                Integer bottomPositionMinutesMultiplier,
                String bottomLabelMinutes
        ) {
            ExpectedValues exp = new ExpectedValues();
            exp.bottomLabelSeconds = bottomLabelSeconds;
            exp.topLabelSeconds = topLabelSeconds;
            exp.topPositionSecondsMultiplier = topPositionSecondsMultiplier;
            exp.bottomPositionSecondsMultiplier = bottomPositionSecondsMultiplier;

            exp.bottomLabelMinutes = bottomLabelMinutes;
            exp.topLabelMinutes = topLabelMinutes;
            exp.topPositionMinutesMultiplier = topPositionMinutesMultiplier;
            exp.bottomPositionMinutesMultiplier = bottomPositionMinutesMultiplier;

            return exp;
        }
    }
}
