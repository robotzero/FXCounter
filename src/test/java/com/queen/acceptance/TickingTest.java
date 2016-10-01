package com.queen.acceptance;

import com.google.code.tempusfugit.temporal.WaitFor;
import com.queen.counter.repository.SavedTimerRepository;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import javafx.beans.property.BooleanProperty;
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
public class TickingTest extends CounterAppIT {

    private SavedTimerRepository repository;
    private BooleanProperty resetOption;

    @DataProvider
    public static Object[][] tickValues() {
                // time, time wait, multi sec top, label top, multi sec bottom, label bottom... minutes.
        return new Object[][] {
                { LocalTime.of(12, 10, 1), 1300, 0, "02", 2, "00", 1, "11", 3, "09" },
                { LocalTime.of(12, 10, 0), 900, 0, "01", 2, "59", 0, "11", 2, "09" },
                { LocalTime.of(12, 10, 59), 900, 0, "00", 2, "58", 1, "11", 3, "09" },
                { LocalTime.of(12, 10, 3), 3600, 1, "00", 3, "58", 0, "11", 2, "09" },
        };
    }

    @Before
    public void setUp() {
        resetOption = this.getBean(BooleanProperty.class);
        repository = this.getBean(SavedTimerRepository.class);
        repository.deleteAll();
    }

    @UseDataProvider("tickValues")
    @Test
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
            String lbl4
    ) {
        // Should we check the database for new clock data.
        resetOption.setValue(true);
        // Prepare clock state;
        repository.create("start", localTime);

        Button reset = assertContext().getNodeFinder().lookup("#reset").queryFirst();
        Button start = assertContext().getNodeFinder().lookup("#start").queryFirst();
        StackPane seconds = assertContext().getNodeFinder().lookup("#paneSeconds").queryFirst();
        StackPane minutes = assertContext().getNodeFinder().lookup("#paneMinutes").queryFirst();

        // Grab the bottom list of rectangles and labels
        List<Node> secondsRectangles = nodeFinder.getRectangles(seconds).get();
        List<Node> secondsLabels = nodeFinder.getLabels(seconds).get();
        List<Node> minutesRectangles = nodeFinder.getRectangles(minutes).get();
        List<Node> minutesLabels = nodeFinder.getLabels(minutes).get();

        // Current height
        int height = (int) ((Rectangle) minutesRectangles.get(0)).getHeight();

        String topIdSeconds = secondsRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height).findAny().get().getId();
        String bottomIdSeconds = secondsRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height * 3).findAny().get().getId();

        String topIdMinutes = minutesRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height).findAny().get().getId();
        String bottomIdMinutes = minutesRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height * 3).findAny().get().getId();

        clickOn(reset);
        clickOn(start);

        try {
            WaitFor.waitUntil(timeout(millis(waitTime)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verifyThat("#paneSeconds", (StackPane s) -> {
            Optional<Node> exTopRectangle = secondsRectangles.stream().filter(rt -> rt.getId().equals(topIdSeconds)).findAny();
            Optional<Node> exBottomRectangle = secondsRectangles.stream().filter(rt -> rt.getId().equals(bottomIdSeconds)).findAny();
            if (exTopRectangle.isPresent() && exBottomRectangle.isPresent()) {
                String labelTop =  secondsLabels.stream().filter(tr -> tr.getId().equals(exTopRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                String labelBottom =  secondsLabels.stream().filter(tr -> tr.getId().equals(bottomIdSeconds)).map(tt -> ((Text) tt).getText()).findAny().get();

                return exTopRectangle.get().getParent().getParent().getTranslateY() == mutli1 * height
                        && labelTop.equals(lbl1)
                        && exBottomRectangle.get().getParent().getParent().getTranslateY() == multi2 * height
                        && labelBottom.equals(lbl2);
            }

            return false;
        });

        verifyThat("#paneMinutes", (StackPane s) -> {
            Optional<Node> exTopRectangle = minutesRectangles.stream().filter(rt -> rt.getId().equals(topIdMinutes)).findAny();
            Optional<Node> exBottomRectangle = minutesRectangles.stream().filter(rt -> rt.getId().equals(bottomIdMinutes)).findAny();
            if (exTopRectangle.isPresent() && exBottomRectangle.isPresent()) {
                String labelTop =  minutesLabels.stream().filter(tr -> tr.getId().equals(exTopRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                String labelBottom =  minutesLabels.stream().filter(tr -> tr.getId().equals(bottomIdMinutes)).map(tt -> ((Text) tt).getText()).findAny().get();

                return exTopRectangle.get().getParent().getParent().getTranslateY() == multi3 * height
                        && labelTop.equals(lbl3)
                        && exBottomRectangle.get().getParent().getParent().getTranslateY() == multi4 * height
                        && labelBottom.equals(lbl4);
            }

            return false;
        });
    }

    @Test
    public void stops_ticking_when_reached_zero() {
        // Should we check the database for new clock data.
        resetOption.setValue(true);
        // Prepare clock state;
        repository.create("start", LocalTime.of(2, 0, 4));

        Button reset = assertContext().getNodeFinder().lookup("#reset").queryFirst();
        Button start = assertContext().getNodeFinder().lookup("#start").queryFirst();
        StackPane seconds = assertContext().getNodeFinder().lookup("#paneSeconds").queryFirst();
        StackPane minutes = assertContext().getNodeFinder().lookup("#paneMinutes").queryFirst();

        // Grab the bottom list of rectangles and labels
        List<Node> secondsRectangles = nodeFinder.getRectangles(seconds).get();
        List<Node> secondsLabels = nodeFinder.getLabels(seconds).get();
        List<Node> minutesRectangles = nodeFinder.getRectangles(minutes).get();
        List<Node> minutesLabels = nodeFinder.getLabels(minutes).get();

        // Current height
        int height = (int) ((Rectangle) minutesRectangles.get(0)).getHeight();

        String topIdSeconds = secondsRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height).findAny().get().getId();
        String bottomIdSeconds = secondsRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height * 3).findAny().get().getId();

        String topIdMinutes = minutesRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height).findAny().get().getId();
        String bottomIdMinutes = minutesRectangles.stream().filter(r -> r.getParent().getParent().getTranslateY() == height * 3).findAny().get().getId();

        clickOn(reset);
        clickOn(start);

        try {
            WaitFor.waitUntil(timeout(millis(TIME_WAIT * 4 * 2)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verifyThat("#paneSeconds", (StackPane s) -> {
            Optional<Node> exTopRectangle = secondsRectangles.stream().filter(rt -> rt.getId().equals(topIdSeconds)).findAny();
            Optional<Node> exBottomRectangle = secondsRectangles.stream().filter(rt -> rt.getId().equals(bottomIdSeconds)).findAny();
            if (exTopRectangle.isPresent() && exBottomRectangle.isPresent()) {
                String labelTop =  secondsLabels.stream().filter(tr -> tr.getId().equals(exTopRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                String labelBottom =  secondsLabels.stream().filter(tr -> tr.getId().equals(bottomIdSeconds)).map(tt -> ((Text) tt).getText()).findAny().get();

                return exTopRectangle.get().getParent().getParent().getTranslateY() == height
                        && labelTop.equals("01")
                        && exBottomRectangle.get().getParent().getParent().getTranslateY() == 3 * height
                        && labelBottom.equals("59");
            }

            return false;
        });

        verifyThat("#paneMinutes", (StackPane s) -> {
            Optional<Node> exTopRectangle = minutesRectangles.stream().filter(rt -> rt.getId().equals(topIdMinutes)).findAny();
            Optional<Node> exBottomRectangle = minutesRectangles.stream().filter(rt -> rt.getId().equals(bottomIdMinutes)).findAny();
            if (exTopRectangle.isPresent() && exBottomRectangle.isPresent()) {
                String labelTop =  minutesLabels.stream().filter(tr -> tr.getId().equals(exTopRectangle.get().getId())).map(tt -> ((Text) tt).getText()).findAny().get();
                String labelBottom =  minutesLabels.stream().filter(tr -> tr.getId().equals(bottomIdMinutes)).map(tt -> ((Text) tt).getText()).findAny().get();

                return exTopRectangle.get().getParent().getParent().getTranslateY() == height
                        && labelTop.equals("01")
                        && exBottomRectangle.get().getParent().getParent().getTranslateY() == 3 * height
                        && labelBottom.equals("59");
            }

            return false;
        });
    }
}
