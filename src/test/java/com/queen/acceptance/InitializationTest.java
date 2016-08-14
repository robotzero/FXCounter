package com.queen.acceptance;

import com.queen.counter.repository.SavedTimerRepository;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import javafx.scene.Group;
import javafx.scene.text.Text;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.testfx.api.FxAssert.assertContext;

@RunWith(DataProviderRunner.class)
public class InitializationTest extends CounterAppIT {

    private SavedTimerRepository repository;

    @DataProvider
    public static Object[][] getClockAndExpectedValues() {
        String[] expectedLabels = {"2", "1", "0", "59"};
        return new Object[][] {
                { LocalTime.of(0, 0, 0), expectedLabels }
        };
    }

    @Before
    public void setUp() {
        repository = this.getBean(SavedTimerRepository.class);
        repository.deleteAll();
    }

    @Test
    @UseDataProvider("getClockAndExpectedValues")
    public void it_initializes_clock_to_correct_values_based_on_provided_time(LocalTime initialClock, String[] expectedLabels) {

        // Prepare clock state;
        repository.create("start", initialClock);

        Group seconds = assertContext().getNodeFinder().lookup("#seconds").queryFirst();
        Group minutes = assertContext().getNodeFinder().lookup("#minutes").queryFirst();

        List<String> visibleSecondsLabels = seconds.getChildren().stream().filter(node -> node.getClass().equals(Text.class))
                                      .map(text -> ((Text) text).getText())
                                      .collect(Collectors.toList());

        for (int i = 0; i < visibleSecondsLabels.size(); i++) {
            Assert.assertEquals(expectedLabels[i], visibleSecondsLabels.get(i));
        }

        List<String> visibleMinutesLabels = minutes.getChildren().stream().filter(node -> node.getClass().equals(Text.class))
                .map(text -> ((Text) text).getText())
                .collect(Collectors.toList());

        for (int i = 0; i < visibleMinutesLabels.size(); i++) {
            Assert.assertEquals(expectedLabels[i], visibleMinutesLabels.get(i));
        }
    }

    @Test
    public void it_initializes_clock_to_zero_values_when_history_is_empty() {
        Group seconds = assertContext().getNodeFinder().lookup("#seconds").queryFirst();
        Group minutes = assertContext().getNodeFinder().lookup("#minutes").queryFirst();

        Assert.assertNull(repository.selectLatest());

        String[] expectedLabels = { "2", "1", "0", "59" };
        List<String> visibleSecondsLabels = seconds.getChildren().stream().filter(node -> node.getClass().equals(Text.class))
                .map(text -> ((Text) text).getText())
                .collect(Collectors.toList());

        for (int i = 0; i < visibleSecondsLabels.size(); i++) {
            Assert.assertEquals(expectedLabels[i], visibleSecondsLabels.get(i));
        }

        List<String> visibleMinutesLabels = minutes.getChildren().stream().filter(node -> node.getClass().equals(Text.class))
                .map(text -> ((Text) text).getText())
                .collect(Collectors.toList());

        for (int i = 0; i < visibleMinutesLabels.size(); i++) {
            Assert.assertEquals(expectedLabels[i], visibleMinutesLabels.get(i));
        }

    }

}
