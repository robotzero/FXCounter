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

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;

@RunWith(DataProviderRunner.class)
public class ResetTest extends CounterAppIT {

    private TimerRepository repository;
    private BooleanProperty resetOption;

    @Before
    public void setUp() {
        resetOption = this.getBean(BooleanProperty.class);
        repository = this.getBean(TimerRepository.class);
        repository.deleteAll();
    }

    @DataProvider
    public static Object[][] resets() {
        return new Object[][]{
                // @formatter:off
                // Seconds
                {
                        // topPosSec, topLblSec, middlPosSec, middlLabelSec
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 11, 10))
                                .withExpectedValues(1, "11", 2, "10", 3, "09", 1, "12", 2, "11", 3, "10", 1, "13", 3, "14",  1, "11")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 11, 10))
                                .withExpectedValues(1, "11", 2, "10", 3, "09", 1, "12", 2, "11", 3, "10", 1, "13", 3, "14",  1, "11")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 4)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 11, 10))
                                .withExpectedValues(1, "11", 2, "10", 3, "09", 1, "12", 2, "11", 3, "10", 1, "13", 3, "14",  1, "11")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 10)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 11, 10))
                                .withExpectedValues(1, "11", 2, "10", 3, "09", 1, "12", 2, "11", 3, "10", 1, "13", 3, "14",  1, "11")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 1)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 11, 10))
                                .withExpectedValues(1, "11", 2, "10", 3, "09", 1, "12", 2, "11", 3, "10", 1, "13", 3, "14",  1, "11")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 7)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 11, 10))
                                .withExpectedValues(1, "11", 2, "10", 3, "09", 1, "12", 2, "11", 3, "10", 1, "13", 3, "14",  1, "11")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 10)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 3, 59))
                                .withExpectedValues(1, "00", 2, "59", 3, "58", 1, "04", 2, "03", 3, "02", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 1)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 3, 59))
                                .withExpectedValues(1, "00", 2, "59", 3, "58", 1, "04", 2, "03", 3, "02", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 2)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 3, 59))
                                .withExpectedValues(1, "00", 2, "59", 3, "58", 1, "04", 2, "03", 3, "02", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 2)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 3)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 0, 0))
                                .withExpectedValues(1, "01", 2, "00", 3, "59", 1, "01", 2, "00", 3, "59", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 2)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 3)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 3)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2)
                                .addReset().close())
                },
                // Minutes
                {
                        // topPosSec, topLblSec, middlPosSec, middlLabelSec
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 8, 6))
                                .withExpectedValues(1, "07", 2, "06", 3, "05", 1, "09", 2, "08", 3, "07", 1, "13", 3, "14",  1, "11")
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 8, 6))
                                .withExpectedValues(1, "07", 2, "06", 3, "05", 1, "09", 2, "08", 3, "07", 1, "13", 3, "14",  1, "11")
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 5)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 8, 6))
                                .withExpectedValues(1, "07", 2, "06", 3, "05", 1, "09", 2, "08", 3, "07", 1, "13", 3, "14",  1, "11")
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 10)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 8, 6))
                                .withExpectedValues(1, "07", 2, "06", 3, "05", 1, "09", 2, "08", 3, "07", 1, "13", 3, "14",  1, "11")
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 1)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 8, 6))
                                .withExpectedValues(1, "07", 2, "06", 3, "05", 1, "09", 2, "08", 3, "07", 1, "13", 3, "14",  1, "11")
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 7)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 8, 6))
                                .withExpectedValues(1, "07", 2, "06", 3, "05", 1, "09", 2, "08", 3, "07", 1, "13", 3, "14",  1, "11")
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 11)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 59, 58))
                                .withExpectedValues(1, "59", 2, "58", 3, "57", 1, "00", 2, "59", 3, "58", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 1)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 59, 58))
                                .withExpectedValues(1, "59", 2, "58", 3, "57", 1, "00", 2, "59", 3, "58", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 2)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 59, 58))
                                .withExpectedValues(1, "59", 2, "58", 3, "57", 1, "00", 2, "59", 3, "58", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 2)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 3)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 0, 0))
                                .withExpectedValues(1, "01", 2, "00", 3, "59", 1, "01", 2, "00", 3, "59", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 2)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 3)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 3)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 2)
                                .addReset().close())
                },
                // Minutes and Seconds
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 0, 0))
                                .withExpectedValues(1, "01", 2, "00", 3, "59", 1, "01", 2, "00", 3, "59", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 1)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 3)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 2)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 0, 0))
                                .withExpectedValues(1, "01", 2, "00", 3, "59", 1, "01", 2, "00", 3, "59", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 1)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 2)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 2)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 0, 0))
                                .withExpectedValues(1, "01", 2, "00", 3, "59", 1, "01", 2, "00", 3, "59", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 4)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 2)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 2)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2)
                                .addReset().close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 58, 1))
                                .withExpectedValues(1, "02", 2, "01", 3, "00", 1, "59", 2, "58", 3, "57", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 4)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 2)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 2)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2)
                                .addReset().close())
                },
                // Scroll after reset
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 0, 0))
                                .withExpectedValues(1, "02", 2, "01", 3, "00", 1, "02", 2, "01", 3, "00", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 1)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1)
                                .addReset()
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 1)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
                                .close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 0, 0))
                                .withExpectedValues(1, "02", 2, "01", 3, "00", 1, "02", 2, "01", 3, "00", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.DOWN, 1)
                                .addReset()
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 2)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 1)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
                                .close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 0, 0))
                                .withExpectedValues(1, "01", 2, "00", 3, "59", 1, "59", 2, "58", 3, "57", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.UP, 2)
                                .addReset()
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 2)
                                .close())
                },
                {
                        com.robotzero.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(9, 58, 1))
                                .withExpectedValues(1, "04", 2, "03", 3, "02", 1, "55", 2, "54", 3, "53", 1, "10", 2, "09",  1, "08")
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 1)
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 3)
                                .addReset()
                                .addScroll(ColumnType.MINUTES, VerticalDirection.DOWN, 4)
                                .addScroll(ColumnType.SECONDS, VerticalDirection.UP, 2)
                                .close())
                },
                //@formatter:on
        };
    }

    @UseDataProvider("resets")
    @Test
    public void reset(Sequence sequence) {

        // Should we check the database for new clock data.
        resetOption.setValue(true);
        // Prepare clock state;
        repository.create("start", sequence.clockStartState);

        Button reset = assertContext().getNodeFinder().lookup("#reset").query();
        StackPane seconds = assertContext().getNodeFinder().lookup("#paneSeconds").query();
        StackPane minutes = assertContext().getNodeFinder().lookup("#paneMinutes").query();
//        StackPane hours = assertContext().getNodeFinder().lookup("#paneHours").queryFirst();

        // Grab the bottom list of rectangles and labels
        List<Node> secondsRectangles = nodeFinder.getRectangles(seconds).get();
        List<Node> minutesRectangles = nodeFinder.getRectangles(minutes).get();
        List<Node> secondsLabels = nodeFinder.getLabels(seconds).get();
        List<Node> minutesLabels = nodeFinder.getLabels(minutes).get();

        // Current height
        int height = (int) ((Rectangle) secondsRectangles.get(0)).getHeight();

        clickOn(reset);

        sequence.steps.forEach(step -> step.execute(this));

        try {
            WaitFor.waitUntil(timeout(millis(TIME_WAIT)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verifyThat("#paneSeconds", (StackPane s) -> {
            Optional<Node> topRectangle = secondsRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height).findAny();
            Optional<Node> middleRectangle = secondsRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height * 2).findAny();
            Optional<Node> bottomRectangle = secondsRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height * 3).findAny();

            if (topRectangle.isPresent() && middleRectangle.isPresent() && bottomRectangle.isPresent()) {
                String labelS =  secondsLabels.stream().filter(tr -> tr.getId().equals(topRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                String labelM =  secondsLabels.stream().filter(tr -> tr.getId().equals(middleRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                String labelB =  secondsLabels.stream().filter(tr -> tr.getId().equals(bottomRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();

                return
                        topRectangle.get().getParent().getParent().getTranslateY() == sequence.expectedValues.topPositionSecondsMultiplier * height
                            && labelS.equals(sequence.expectedValues.topLabelSeconds)
                            && middleRectangle.get().getParent().getParent().getTranslateY() == sequence.expectedValues.middlePositionSecondsMultiplier * height
                            && labelM.equals(sequence.expectedValues.middleLabelSeconds)
                            && bottomRectangle.get().getParent().getParent().getTranslateY() == sequence.expectedValues.bottomPositionSecondsMultiplier * height
                            && labelB.equals(sequence.expectedValues.bottomLabelSeconds);
            }

            return false;
        });

        verifyThat("#paneMinutes", (StackPane s) -> {
            Optional<Node> topRectangle = minutesRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height).findAny();
            Optional<Node> middleRectangle = minutesRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height * 2).findAny();
            Optional<Node> bottomRectangle = minutesRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height * 3).findAny();

            if (topRectangle.isPresent() && middleRectangle.isPresent() && bottomRectangle.isPresent()) {

                String labelS =  minutesLabels.stream().filter(tr -> tr.getId().equals(topRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                String labelM =  minutesLabels.stream().filter(tr -> tr.getId().equals(middleRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                String labelB =  minutesLabels.stream().filter(tr -> tr.getId().equals(bottomRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();

                return
                        topRectangle.get().getParent().getParent().getTranslateY() == sequence.expectedValues.topPositionMinutesMultiplier * height
                                && labelS.equals(sequence.expectedValues.topLabelMinutes)
                                && middleRectangle.get().getParent().getParent().getTranslateY() == sequence.expectedValues.middlePositionMinutesMultiplier * height
                                && labelM.equals(sequence.expectedValues.middleLabelMinutes)
                                && bottomRectangle.get().getParent().getParent().getTranslateY() == sequence.expectedValues.bottomPositionMinutesMultiplier * height
                                && labelB.equals(sequence.expectedValues.bottomLabelMinutes);
            }

            return false;
        });
    }
}
