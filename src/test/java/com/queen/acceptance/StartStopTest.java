package com.queen.acceptance;

import com.google.code.tempusfugit.temporal.WaitFor;
import com.queen.counter.repository.SavedTimerRepository;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalTime;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;

@RunWith(DataProviderRunner.class)
public class StartStopTest extends CounterAppIT {

    private Button startButton, stopButton, resetButton;
    private BooleanProperty resetOption;
    private SavedTimerRepository repository;

    @Before
    public void setUp() {

        startButton = assertContext().getNodeFinder().lookup("#start").queryFirst();
        stopButton = assertContext().getNodeFinder().lookup("#stop").queryFirst();
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

        verifyThat("#seconds", (Group seconds) -> {
            boolean present = seconds.getChildren().stream().filter(node -> node.getClass().equals(Text.class)).filter(text -> ((Text) text).getText().equals("4")).findAny().isPresent();
            return !present;
        });

        verifyThat("#minutes", (Group minutes) -> {
            boolean present = minutes.getChildren().stream().filter(node -> node.getClass().equals(Text.class)).filter(text -> ((Text) text).getText().equals("12")).findAny().isPresent();
            return !present;
        });
    }

    @Test
    public void while_animation_running_stop_and_reset_buttons_are_disabled()
    {

    }
}
