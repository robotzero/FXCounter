package com.queen.acceptance;

import com.queen.counter.repository.SavedTimerRepository;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
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

    @Before
    public void setUp() {
        repository = this.getBean(SavedTimerRepository.class);
        repository.deleteAll();
    }

    @Test
    public void it_correctly_initializes_clock_to_0() {

        // Prepare clock state;
        repository.create("start", LocalTime.of(0, 0, 0));

        Group seconds = assertContext().getNodeFinder().lookup("#seconds").queryFirst();
        Group minutes = assertContext().getNodeFinder().lookup("#minutes").queryFirst();

        List<String> visibleLabels = seconds.getChildren().stream().filter(node -> node.getClass().equals(Text.class))
                                      .map(text -> ((Text) text).getText())
                                      .collect(Collectors.toList());

        String[] expectedLabels = {"2", "1", "0", "59"};

        for (int i = 0; i < visibleLabels.size(); i++) {
            Assert.assertEquals(expectedLabels[i], visibleLabels.get(i));
        }
    }
}
