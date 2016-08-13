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
                {sequence(
                        expected(60, "14", 240, "11", 0, "18", 180, "14"),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 1)
                )},
                {sequence(
                        expected(60, "14", 240, "11", 0, "18", 180, "14"),
                        step(ColumnType.SECONDS, VerticalDirection.UP, 1)
                )}
                //@formatter:on
        };
    }

    // DOWN
//                { 1, VerticalDirection.DOWN, "#seconds", 180, 0, "10", "13"},
//                { 2, VerticalDirection.DOWN, "#seconds", 120, 180, "10" , "9"},
//                { 3, VerticalDirection.DOWN, "#seconds", 60, 120, "10", "9" },
//                { 4, VerticalDirection.DOWN, "#seconds", 0, 60, "10", "9" },
//                { 5, VerticalDirection.DOWN, "#seconds", 180, 0, "6", "9" },
//                { 6, VerticalDirection.DOWN, "#seconds", 120, 180, "6", "5" },
//                { 7, VerticalDirection.DOWN, "#seconds", 60, 120, "6", "5" },
//                { 8, VerticalDirection.DOWN, "#seconds", 0, 60, "6", "5" },
//                { 9, VerticalDirection.DOWN, "#seconds", 180, 0, "2", "5" },
//                { 10, VerticalDirection.DOWN, "#seconds", 120, 180, "2", "1" },
//                // UP

//                { 2, VerticalDirection.UP, "#seconds", 120, 60, "14", "15" },
//                { 3, VerticalDirection.UP, "#seconds", 180, 120, "14", "15" },
//                { 4, VerticalDirection.UP, "#seconds", 240, 180, "14", "15" },
//                { 5, VerticalDirection.UP, "#seconds", 60, 240, "18", "15" },
//                { 6, VerticalDirection.UP, "#seconds", 120, 60, "18", "19" },
//                { 7, VerticalDirection.UP, "#seconds", 180, 120, "18", "19" },
//                { 8, VerticalDirection.UP, "#seconds", 240, 180, "18", "19" },
//                { 9, VerticalDirection.UP, "#seconds", 60, 240, "22", "19" },

    @DataProvider
    public static Object[][] basicSecondsMinutesScrollsSetupUpDown() {


        // Number of scrolls, direction, group, timewait, expected new translate, expected label
        return new Object[][] {
                { 1, VerticalDirection.UP, "#seconds", 650, 60, "14" },
                { 2, VerticalDirection.UP, "#seconds", 650, 120, "14" },
                { 3, VerticalDirection.UP, "#seconds", 650, 180, "14" },
                { 4, VerticalDirection.UP, "#minutes", 650, 240, "18" },
                { 5, VerticalDirection.UP, "#seconds", 650, 60, "18" },
                { 6, VerticalDirection.UP, "#minutes", 650, 120, "22" },
                { 7, VerticalDirection.UP, "#seconds", 650, 180, "18" },
                { 8, VerticalDirection.UP, "#seconds", 650, 240, "18" },
                { 9, VerticalDirection.UP, "#seconds", 650, 60, "22" },
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
                scroll(step.scrollsNumber, step.direction);
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
                String lb =  minutes.getChildren().stream().filter(rk -> rk.getClass().equals(Text.class)).filter(tr -> tr.getId().equals(bottomIdSeconds)).map(tt -> ((Text) tt).getText()).findAny().get();
                return exBottomRectangle.get().getTranslateY() == sequence.expectedValues.bottomPositionMinutes && lb.equals(sequence.expectedValues.bottomLabelMinutes);
            }

            return false;
        });
    }

    // @TODO check minutes group as well.
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

        public static Sequence sequence(ExpectedValues expectedValues, Step ...step) {
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
