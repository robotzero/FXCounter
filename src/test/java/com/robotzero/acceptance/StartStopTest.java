package com.robotzero.acceptance;

import com.google.code.tempusfugit.temporal.WaitFor;
import com.robotzero.counter.domain.clock.ClockRepository;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
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
public class StartStopTest extends CounterAppIT {

    private Button startButton, resetButton;
    private BooleanProperty resetOption;
    private ClockRepository repository;

    @Before
    public void setUp() {

        startButton = assertContext().getNodeFinder().lookup("#start").query();
        resetButton = assertContext().getNodeFinder().lookup("#reset").query();

        resetOption = this.getBean(BooleanProperty.class);
        repository = this.getBean(ClockRepository.class);
        repository.deleteAll();
    }

    @Test
    public void start_button_starts_animation() {
        resetOption.set(true);
        repository.create("latest", LocalTime.of(0, 10, 3));
        clickOn(resetButton);
        clickOn(startButton);

        try {
            WaitFor.waitUntil(timeout(millis(4000)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verifyThat("#paneSeconds", (StackPane seconds) -> {
            List<Node> secondsLabels = nodeFinder.getLabels(seconds).get();
            boolean present = secondsLabels.stream().anyMatch(text -> ((Text) text).getText().equals("04"));
            return !present;
        });

        verifyThat("#paneMinutes", (StackPane minutes) -> {
            List<Node> minutesLabels = nodeFinder.getLabels(minutes).get();
            boolean present = minutesLabels.stream().anyMatch(text -> ((Text) text).getText().equals("12"));
            return !present;
        });
    }

    @Test
    public void while_animation_is_running_stop_and_reset_buttons_are_disabled()
    {
        resetOption.set(true);
        repository.create("latest", LocalTime.of(0, 10, 3));
        clickOn(resetButton);
        clickOn(startButton);

        try {
            WaitFor.waitUntil(timeout(millis(1000)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clickOn(resetButton);
        clickOn(startButton);

        try {
            WaitFor.waitUntil(timeout(millis(3000)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verifyThat("#paneSeconds", (StackPane seconds) -> {
            List<Node> secondsLabels = nodeFinder.getLabels(seconds).get();
            boolean present = secondsLabels.stream().anyMatch(text -> ((Text) text).getText().equals("04"));
            return !present;
        });

        verifyThat("#paneMinutes", (StackPane minutes) -> {
            List<Node> minutesLabels = nodeFinder.getLabels(minutes).get();
            boolean present = minutesLabels.stream().anyMatch(text -> ((Text) text).getText().equals("12"));
            return !present;
        });
    }

    @Test
    public void disallow_start_when_clock_already_at_zero() {
        // Should we check the database for new clock data.
        resetOption.setValue(true);
        // Prepare clock state;
        repository.create("start", LocalTime.of(0, 0, 0));

        Button reset = assertContext().getNodeFinder().lookup("#reset").query();
        Button start = assertContext().getNodeFinder().lookup("#start").query();
        StackPane seconds = assertContext().getNodeFinder().lookup("#paneSeconds").query();
        StackPane minutes = assertContext().getNodeFinder().lookup("#paneMinutes").query();

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
            WaitFor.waitUntil(timeout(millis(TIME_WAIT * 2)));
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

    @Test
    public void start_button_changes_to_pause_after_clicking_start_and_back_to_start() {

        // Should we check the database for new clock data.
        resetOption.setValue(true);
        // Prepare clock state;
        repository.create("start", LocalTime.of(2, 2, 0));

        Button reset = assertContext().getNodeFinder().lookup("#reset").query();
        Button start = assertContext().getNodeFinder().lookup("#start").query();

        clickOn(reset);
        clickOn(start);

        verifyThat("#start", (Button b) -> b.textProperty().get().equals("Pause"));

        clickOn(start);

        verifyThat("#start", (Button b) -> b.textProperty().get().equals("Start"));
    }
}
