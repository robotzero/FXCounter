package com.queen.acceptance;

import com.google.code.tempusfugit.temporal.WaitFor;
import com.queen.counter.repository.SavedTimerRepository;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalTime;
import java.util.List;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;

@RunWith(DataProviderRunner.class)
public class StartStopTest extends CounterAppIT {

    private Button startButton, resetButton;
    private BooleanProperty resetOption;
    private SavedTimerRepository repository;

    @Before
    public void setUp() {

        startButton = assertContext().getNodeFinder().lookup("#start").queryFirst();
        resetButton = assertContext().getNodeFinder().lookup("#reset").queryFirst();

        resetOption = this.getBean(BooleanProperty.class);
        repository = this.getBean(SavedTimerRepository.class);
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
}
