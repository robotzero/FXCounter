package com.queen.acceptance;

import com.google.code.tempusfugit.temporal.WaitFor;
import com.queen.acceptance.fixtures.Sequence;
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
public class ResetTest extends CounterAppIT {

    private SavedTimerRepository repository;
    private BooleanProperty resetOption;

    @Before
    public void setUp() {
        resetOption = this.getBean(BooleanProperty.class);
        repository = this.getBean(SavedTimerRepository.class);
        repository.deleteAll();
    }

    @DataProvider
    public static Object[][] resets() {
        return new Object[][]{
                // @formatter:off
                // Seconds UP
                {
                        // topPosSec, topLblSec, middlPosSec, middlLabelSec
                        com.queen.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 11, 10))
                                .withExpectedValues(1, "11", 2, "10", 3, "09", 1, "12", 2, "11", 3, "10", 1, "13", 3, "14",  1, "11")
                                .addStep(ColumnType.SECONDS, VerticalDirection.UP, 1).close())

                },
                {
                        com.queen.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 11, 10))
                                .withExpectedValues(1, "11", 2, "10", 3, "09", 1, "12", 2, "11", 3, "10", 1, "13", 3, "14",  1, "11")
                                .addStep(ColumnType.SECONDS, VerticalDirection.UP, 5).close())

                },
                {
                        com.queen.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 11, 10))
                                .withExpectedValues(1, "11", 2, "10", 3, "09", 1, "12", 2, "11", 3, "10", 1, "13", 3, "14",  1, "11")
                                .addStep(ColumnType.SECONDS, VerticalDirection.UP, 10).close())

                },
                {
                        com.queen.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 11, 10))
                                .withExpectedValues(1, "11", 2, "10", 3, "09", 1, "12", 2, "11", 3, "10", 1, "13", 3, "14",  1, "11")
                                .addStep(ColumnType.SECONDS, VerticalDirection.DOWN, 1).close())

                },
                {
                        com.queen.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 11, 10))
                                .withExpectedValues(1, "11", 2, "10", 3, "09", 1, "12", 2, "11", 3, "10", 1, "13", 3, "14",  1, "11")
                                .addStep(ColumnType.SECONDS, VerticalDirection.DOWN, 7).close())

                },
                {
                        com.queen.acceptance.fixtures.Sequence.create(config -> config
                                .withStartClock(LocalTime.of(12, 11, 10))
                                .withExpectedValues(1, "11", 2, "10", 3, "09", 1, "12", 2, "11", 3, "10", 1, "13", 3, "14",  1, "11")
                                .addStep(ColumnType.SECONDS, VerticalDirection.DOWN, 11).close())

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

        Button reset = assertContext().getNodeFinder().lookup("#reset").queryFirst();
        StackPane seconds = assertContext().getNodeFinder().lookup("#paneSeconds").queryFirst();
        StackPane minutes = assertContext().getNodeFinder().lookup("#paneMinutes").queryFirst();
//        StackPane hours = assertContext().getNodeFinder().lookup("#paneHours").queryFirst();

        // Grab the bottom list of rectangles and labels
        List<Node> secondsRectangles = nodeFinder.getRectangles(seconds).get();
        List<Node> minutesRectangles = nodeFinder.getRectangles(minutes).get();
        List<Node> secondsLabels = nodeFinder.getLabels(seconds).get();
        List<Node> minutesLabels = nodeFinder.getLabels(minutes).get();

        // Current height
        int height = (int) ((Rectangle) secondsRectangles.get(0)).getHeight();

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

        clickOn(reset);

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
