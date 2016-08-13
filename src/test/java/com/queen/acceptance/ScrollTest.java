package com.queen.acceptance;

import com.google.code.tempusfugit.temporal.WaitFor;
import com.queen.counter.domain.ColumnType;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import javafx.geometry.VerticalDirection;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @DataProvider
    public static Object[][] scrolls() {
        return new Object[][]{
                //@formatter:off
                // Seconds UP
                {sequence(
                        expected(60, "14", 240, "11", 0, "18", 180, "14"),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 1)
                )},
                {sequence(
                        expected(120, "14", 60, "15", 0, "18", 180, "14"),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 2)
                )},
                {sequence(
                        expected(180, "14", 120, "15", 0, "18", 180, "14"),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 3)
                )},
                {sequence(
                        expected(240, "14", 180, "15", 0, "18", 180, "14"),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 4)
                )},
                {sequence(
                        expected(60, "18", 240, "15", 0, "18", 180, "14"),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 5)
                )},
                {sequence(
                        expected(120, "18", 60, "19", 0, "18", 180, "14"),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 6)
                )},
                // Seconds DOWN
                {sequence(
                        expected(180, "10", 120, "11", 0, "18", 180, "14"),
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 1)
                )},
                {sequence(
                        expected(120, "10", 60, "11", 0, "18", 180, "14"),
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 2)
                )},
                {sequence(
                        expected(60, "10", 0, "11", 0, "18", 180, "14"),
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 3)
                )},
                {sequence(
                        expected(0, "10", 180, "7", 0, "18", 180, "14"),
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 4)
                )},
                {sequence(
                        expected(180, "6", 120, "7", 0, "18", 180, "14"),
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 5)
                )},
                {sequence(
                        expected(120, "6", 60, "7", 0, "18", 180, "14"),
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 6)
                )},
                // Minutes UP
                {sequence(
                        expected(0, "14", 180, "11", 60, "18", 240, "15"),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 1)
                )},
                {sequence(
                        expected(0, "14", 180, "11", 120, "18", 60, "19"),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 2)
                )},
                {sequence(
                        expected(0, "14", 180, "11", 180, "18", 120, "19"),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 3)
                )},
                {sequence(
                        expected(0, "14", 180, "11", 240, "18", 180, "19"),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 4)
                )},
                {sequence(
                        expected(0, "14", 180, "11", 60, "22", 240, "19"),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 5)
                )},
                {sequence(
                        expected(0, "14", 180, "11", 120, "22", 60, "23"),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 6)
                )},
                // Minutes DOWN
                {sequence(
                        expected(0, "14", 180, "11", 180, "14", 120, "15"),
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 1)
                )},
                {sequence(
                        expected(0, "14", 180, "11", 120, "14", 60, "15"),
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 2)
                )},
                {sequence(
                        expected(0, "14", 180, "11", 60, "14", 0, "15"),
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 3)
                )},
                {sequence(
                        expected(0, "14", 180, "11", 0, "14", 180, "11"),
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 4)
                )},
                {sequence(
                        expected(0, "14", 180, "11", 180, "10", 120, "11"),
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 5)
                )},
                {sequence(
                        expected(0, "14", 180, "11", 120, "10", 60, "11"),
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 6)
                )},
                // Seconds and Minutes
                {sequence(
                        expected(120, "10", 60, "11", 180, "18", 120, "19"),
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 2),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 3)
                )},
                {sequence(
                        expected(120, "10", 60, "11", 180, "18", 120, "19"),
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 2),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 3)
                )},
                {sequence(
                        expected(240, "14", 180, "15", 60, "14", 240, "11"),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 3),
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 4),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 1),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 1)
                )},
                {sequence(
                        expected(0, "14", 180, "11", 0, "18", 180, "15"),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 1),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 1),
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 1),
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 1)
                )},
                {sequence(
                        expected(0, "14", 180, "11", 120, "18", 60, "19"),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 1),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 1),
                        step(ColumnType.SECONDS, VerticalDirection.DOWN, 1),
                        step(ColumnType.MINUTES, VerticalDirection.DOWN, 1),
                        step(ColumnType.MINUTES, VerticalDirection.UP, 2)
                )},
                //@formatter:on
        };
    }

    @Test
    @UseDataProvider("scrolls")
    public void scroll(Sequence sequence) {

        Group seconds = assertContext().getNodeFinder().lookup("#seconds").queryFirst();
        Group minutes = assertContext().getNodeFinder().lookup("#minutes").queryFirst();

        String topIdSeconds = seconds.getChildren().stream().filter(r -> r.getClass().equals(Rectangle.class)).filter(r -> r.getTranslateY() == TOP_NODE_LOCATION).findAny().get().getId();
        String bottomIdSeconds = seconds.getChildren().stream().filter(r -> r.getClass().equals(Rectangle.class)).filter(r -> r.getTranslateY() == BOTTOM_NODE_LOCATION).findAny().get().getId();

        String topIdMinutes = minutes.getChildren().stream().filter(r -> r.getClass().equals(Rectangle.class)).filter(r -> r.getTranslateY() == TOP_NODE_LOCATION).findAny().get().getId();
        String bottomIdMinutes = minutes.getChildren().stream().filter(r -> r.getClass().equals(Rectangle.class)).filter(r -> r.getTranslateY() == BOTTOM_NODE_LOCATION).findAny().get().getId();

        sequence.steps.forEach(step -> {
            moveTo("#" + step.columnType.name().toLowerCase());
            IntStream.range(0, step.scrollsNumber).forEach(i -> {
                scroll(step.direction);
                try {
                    WaitFor.waitUntil(timeout(millis(TIME_WAIT)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        });

        verifyThat("#seconds", (Group s) -> {
            Optional<Node> exTopRectangle = seconds.getChildren().stream().filter(rs -> rs.getClass().equals(Rectangle.class)).filter(rt -> rt.getId().equals(topIdSeconds)).findAny();
            Optional<Node> exBottomRectangle = seconds.getChildren().stream().filter(rs -> rs.getClass().equals(Rectangle.class)).filter(rt -> rt.getId().equals(bottomIdSeconds)).findAny();
            if (exTopRectangle.isPresent()) {
                String label =  seconds.getChildren().stream().filter(rk -> rk.getClass().equals(Text.class)).filter(tr -> tr.getId().equals(exTopRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                return exTopRectangle.get().getTranslateY() == sequence.expectedValues.topPositionSeconds && label.equals(sequence.expectedValues.topLabelSeconds);
            }

            if (exBottomRectangle.isPresent()) {
                String lb =  seconds.getChildren().stream().filter(rk -> rk.getClass().equals(Text.class)).filter(tr -> tr.getId().equals(bottomIdSeconds)).map(tt -> ((Text) tt).getText()).findAny().get();
                return exBottomRectangle.get().getTranslateY() == sequence.expectedValues.bottomPositionSeconds && lb.equals(sequence.expectedValues.bottomLabelSeconds);
            }

            return false;
        });

        verifyThat("#minutes", (Group m) -> {
            Optional<Node> exTopRectangle = minutes.getChildren().stream().filter(rs -> rs.getClass().equals(Rectangle.class)).filter(rt -> rt.getId().equals(topIdMinutes)).findAny();
            Optional<Node> exBottomRectangle = minutes.getChildren().stream().filter(rs -> rs.getClass().equals(Rectangle.class)).filter(rt -> rt.getId().equals(bottomIdMinutes)).findAny();
            if (exTopRectangle.isPresent()) {
                String label =  minutes.getChildren().stream().filter(rk -> rk.getClass().equals(Text.class)).filter(tr -> tr.getId().equals(exTopRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                return exTopRectangle.get().getTranslateY() == sequence.expectedValues.topPositionMinutes && label.equals(sequence.expectedValues.topLabelMinutes);
            }

            if (exBottomRectangle.isPresent()) {
                String lb =  minutes.getChildren().stream().filter(rk -> rk.getClass().equals(Text.class)).filter(tr -> tr.getId().equals(bottomIdMinutes)).map(tt -> ((Text) tt).getText()).findAny().get();
                return exBottomRectangle.get().getTranslateY() == sequence.expectedValues.bottomPositionMinutes && lb.equals(sequence.expectedValues.bottomLabelMinutes);
            }

            return false;
        });
    }

    // @TODO check cases around 0 and 50
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
        ExpectedValues expectedValues;

        static Sequence sequence(ExpectedValues expectedValues, Step ...step) {
            Sequence sequence = new Sequence();
            sequence.steps.addAll(asList(step));
            sequence.expectedValues = expectedValues;
            return sequence;
        }
    }

    static class ExpectedValues {
        String topLabelSeconds;
        String bottomLabelSeconds;
        Integer topPositionSeconds;
        Integer bottomPositionSeconds;

        String topLabelMinutes;
        String bottomLabelMinutes;
        Integer topPositionMinutes;
        Integer bottomPositionMinutes;

        static ExpectedValues expected(
                Integer topPositionSeconds,
                String topLabelSeconds,
                Integer bottomPositionSeconds,
                String bottomLabelSeconds,
                Integer topPositionMinutes,
                String topLabelMinutes,
                Integer bottomPositionMinutes,
                String bottomLabelMinutes
        ) {
            ExpectedValues exp = new ExpectedValues();
            exp.bottomLabelSeconds = bottomLabelSeconds;
            exp.topLabelSeconds = topLabelSeconds;
            exp.topPositionSeconds = topPositionSeconds;
            exp.bottomPositionSeconds = bottomPositionSeconds;

            exp.bottomLabelMinutes = bottomLabelMinutes;
            exp.topLabelMinutes = topLabelMinutes;
            exp.topPositionMinutes = topPositionMinutes;
            exp.bottomPositionMinutes = bottomPositionMinutes;

            return exp;
        }
    }
}
