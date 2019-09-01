package com.robotzero.acceptance;

import com.google.code.tempusfugit.temporal.WaitFor;
import com.robotzero.acceptance.fixtures.Sequence;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.clock.TimerRepository;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;

@RunWith(DataProviderRunner.class)
public class ScrollTest extends CounterAppIT {

    private TimerRepository repository;
    private BooleanProperty resetOption;

    @Before
    public void setUp() {
        resetOption = this.getBean(BooleanProperty.class);
        repository = this.getBean(TimerRepository.class);
        repository.deleteAll();
    }

    @DataProvider
    public static Object[][] scrolls() {
        return new Object[][]{
                // @formatter:off
                // Seconds UP
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(1, "14", 4, "11", 0, "18", 3, "15")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(2, "14", 1, "15", 0, "18", 3, "15")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(3, "14", 2, "15", 0, "18", 3, "15")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 3).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(4, "14", 3, "15", 0, "18", 3, "15")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 4).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(1, "18", 4, "15", 0, "18", 3, "15")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 5).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(2, "18", 1, "19", 0, "18", 3, "15")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 6).close())
                },
                // Seconds DOWN
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(3, "10", 2, "11", 0, "18", 3, "15")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 1).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(2, "10", 1, "11", 0, "18", 3, "15")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 2).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(1, "10", 0, "11", 0, "18", 3, "15")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 3).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(0, "10", 3, "07", 0, "18", 3, "15")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 4).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(3, "06", 2, "07", 0, "18", 3, "15")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 5).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(2, "06", 1, "07", 0, "18", 3, "15")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 6).close())
                },
                // Minutes UP
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(0, "14", 3, "11", 1, "18", 4, "15")
                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(0, "14", 3, "11", 2, "18", 1, "19")
                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 2).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(0, "14", 3, "11", 3, "18", 2, "19")
                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 3).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(0, "14", 3, "11", 4, "18", 3, "19")
                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 4).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(0, "14", 3, "11", 1, "22", 4, "19")
                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 5).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(0, "14", 3, "11", 2, "22", 1, "23")
                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 6).close())
                },
                // Minutes DOWN
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(0, "14", 3, "11", 3, "14", 2, "15")
                            .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 1).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(0, "14", 3, "11", 2, "14", 1, "15")
                            .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 2).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(0, "14", 3, "11", 1, "14", 0, "15")
                            .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 3).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(0, "14", 3, "11", 0, "14", 3, "11")
                            .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 4).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(0, "14", 3, "11", 3, "10", 2, "11")
                            .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 5).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(0, "14", 3, "11", 2, "10", 1, "11")
                            .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 6).close())
                },
                // Seconds and Minutes
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(2, "10", 1, "11", 3, "18", 2, "19")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 2)
                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 3).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                            .withStartClock(DEFAULT_CLOCK_STATE)
                            .withExpectedValues(4, "14", 3, "15", 1, "14", 4, "11")
                            .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 3)
                            .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 4)
                            .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
                            .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1).close())
                },
                {
                   com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                           .withStartClock(DEFAULT_CLOCK_STATE)
                           .withExpectedValues(0, "14", 3, "11", 0, "18", 3, "15")
                           .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
                           .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1)
                           .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 1)
                           .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 1).close())
                },
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                          .withStartClock(DEFAULT_CLOCK_STATE)
                          .withExpectedValues(0, "14", 3, "11", 2, "18", 1, "19")
                          .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
                          .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1)
                          .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 1)
                          .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 1)
                          .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 2).close())
                },
                // Different clock start state
                {
                    com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                           .withStartClock(LocalTime.of(0, 0, 0))
                           .withExpectedValues(3, "02", 2, "03", 1, "58", 0, "59")
                           .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2)
                           .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 3)
                           .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1).close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(0, 0, 0))
                                .withExpectedValues(2, "02", 1, "03", 3, "58", 2, "59")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 1)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1).close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(0, 59, 56))
                                .withExpectedValues(1, "58", 0, "59", 3, "57", 2, "58")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 1)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 1).close())
                },
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

        Button reset = assertContext().getNodeFinder().lookup("#reset").query();
        StackPane seconds = assertContext().getNodeFinder().lookup("#seconds").query();
        StackPane minutes = assertContext().getNodeFinder().lookup("#minutes").query();

        // Grab the bottom list of rectangles and labels
        List<Node> secondsRectangles = nodeFinder.getRectangles(seconds).get();
        List<Node> minutesRectangles = nodeFinder.getRectangles(minutes).get();
        List<Node> secondsLabels = nodeFinder.getLabels(seconds).get();
        List<Node> minutesLabels = nodeFinder.getLabels(minutes).get();

        // Current height
        int height = (int) ((Rectangle) secondsRectangles.get(0)).getHeight();

        String topIdSeconds = secondsRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == TOP_NODE_LOCATION).findAny().get().getId();
        String bottomIdSeconds = secondsRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height * 3).findAny().get().getId();
        String topIdMinutes = minutesRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == TOP_NODE_LOCATION).findAny().get().getId();
        String bottomIdMinutes = minutesRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height * 3).findAny().get().getId();

        clickOn(reset);
        sequence.steps.forEach(step -> {
            step.execute(this);
        });

        verifyThat("#paneSeconds", (StackPane s) -> {
            Optional<Node> exTopRectangle = secondsRectangles.stream().filter(rt -> rt.getId().equals(topIdSeconds)).findAny();
            Optional<Node> exBottomRectangle = secondsRectangles.stream().filter(rt -> rt.getId().equals(bottomIdSeconds)).findAny();
            if (exTopRectangle.isPresent() && exBottomRectangle.isPresent()) {
                String labelTop =  secondsLabels.stream().filter(tr -> tr.getId().equals(exTopRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                String labelBottom =  secondsLabels.stream().filter(tr -> tr.getId().equals(bottomIdSeconds)).map(tt -> ((Text) tt).getText()).findAny().get();

                return exTopRectangle.get().getParent().getParent().getTranslateY() == sequence.expectedValues.topPositionSecondsMultiplier * height
                        && labelTop.equals(sequence.expectedValues.topLabelSeconds)
                        && exBottomRectangle.get().getParent().getParent().getTranslateY() == sequence.expectedValues.bottomPositionSecondsMultiplier * height
                        && labelBottom.equals(sequence.expectedValues.bottomLabelSeconds);
            }

            return false;
        });

        verifyThat("#minutes", (StackPane s) -> {
            Optional<Node> exTopRectangle = minutesRectangles.stream().filter(rt -> rt.getId().equals(topIdMinutes)).findAny();
            Optional<Node> exBottomRectangle = minutesRectangles.stream().filter(rt -> rt.getId().equals(bottomIdMinutes)).findAny();
            if (exTopRectangle.isPresent() && exBottomRectangle.isPresent()) {
                String labelTop =  minutesLabels.stream().filter(tr -> tr.getId().equals(exTopRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                String labelBottom =  minutesLabels.stream().filter(tr -> tr.getId().equals(bottomIdMinutes)).map(tt -> ((Text) tt).getText()).findAny().get();

                return exTopRectangle.get().getParent().getParent().getTranslateY() == sequence.expectedValues.topPositionMinutesMultiplier * height
                        && labelTop.equals(sequence.expectedValues.topLabelMinutes)
                        && exBottomRectangle.get().getParent().getParent().getTranslateY() == sequence.expectedValues.bottomPositionMinutesMultiplier * height
                        && labelBottom.equals(sequence.expectedValues.bottomLabelMinutes);
            }

            return false;
        });
    }

    @Test
    public void unable_to_scroll_after_pressing_start()
    {
        // Should we check the database for new clock data.
        resetOption.setValue(true);
        // Prepare clock state;
        repository.create("start", DEFAULT_CLOCK_STATE);

        Button reset = assertContext().getNodeFinder().lookup("#reset").query();
        Button start = assertContext().getNodeFinder().lookup("#start").query();
        StackPane minutes = assertContext().getNodeFinder().lookup("#minutes").query();

        // Grab the bottom list of rectangles and labels
        List<Node> minutesRectangles = nodeFinder.getRectangles(minutes).get();
        List<Node> minutesLabels = nodeFinder.getLabels(minutes).get();

        // Current height
        int height = (int) ((Rectangle) minutesRectangles.get(0)).getHeight();

        String topIdMinutes = minutesRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == TOP_NODE_LOCATION).findAny().get().getId();
        String bottomIdMinutes = minutesRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height * 3).findAny().get().getId();

        clickOn(reset);
        clickOn(start);

        moveTo("#paneMinutes");

        IntStream.range(0, 3).forEach(i -> {
            scroll(VerticalDirection.DOWN);
            try {
                WaitFor.waitUntil(timeout(millis(CounterAppIT.TIME_WAIT)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        verifyThat("#minutes", (StackPane s) -> {
            Optional<Node> exTopRectangle = minutesRectangles.stream().filter(rt -> rt.getId().equals(topIdMinutes)).findAny();
            Optional<Node> exBottomRectangle = minutesRectangles.stream().filter(rt -> rt.getId().equals(bottomIdMinutes)).findAny();
            if (exTopRectangle.isPresent() && exBottomRectangle.isPresent()) {
                String labelTop =  minutesLabels.stream().filter(tr -> tr.getId().equals(exTopRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                String labelBottom =  minutesLabels.stream().filter(tr -> tr.getId().equals(bottomIdMinutes)).map(tt -> ((Text) tt).getText()).findAny().get();

                return exTopRectangle.get().getParent().getParent().getTranslateY() == 0
                        && labelTop.equals("18")
                        && exBottomRectangle.get().getParent().getParent().getTranslateY() == 3 * height
                        && labelBottom.equals("15");
            }

            return false;
        });
    }
}
