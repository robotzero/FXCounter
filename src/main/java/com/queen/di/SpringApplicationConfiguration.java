package com.queen.di;

import com.airhacks.afterburner.views.FXMLView;
import com.queen.animator.Animator;
import com.queen.configuration.SceneConfiguration;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.clock.ClockPresenter;
import com.queen.counter.clock.ClockView;
import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.UIService;
import com.queen.counter.repository.SavedTimerRepository;
import com.queen.counter.repository.SavedTimerSqlliteJdbcRepository;
import com.queen.counter.service.Populator;
import com.queen.counter.service.Ticker;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.reactfx.EventSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SpringApplicationConfiguration {

    private Animator animator = new Animator();
    private InMemoryCachedServiceLocator cache = new InMemoryCachedServiceLocator();
    private FXMLView clockView = new ClockView();
    private UIService uiService = new UIService();
    private EventSource seconds = new EventSource();
    private EventSource minutes = new EventSource();
    private EventSource hours = new EventSource();

    private EventSource<Void> playMinutes = new EventSource<>();
    private EventSource<Void> playHours = new EventSource<>();

    private EventSource<Integer> deltaEvent = new EventSource<>();
    private EventSource<Integer> deltaStreamSeconds = new EventSource<>();
    private EventSource<Integer> deltaStreamMinutes = new EventSource<>();
    private EventSource<Integer> deltaStreamHours = new EventSource<>();

    @Bean
    public FXMLView clockView() {
        return clockView;
    }

    @Bean
    public ClockPresenter clockPresenter() {
        return new ClockPresenter();
    }

    @Bean
    public SceneConfiguration sceneConfiguration() {
        return new SceneConfiguration();
    }

    @Bean
    public EventSource<Void> PlayMinutes() {
        return playMinutes;
    }

    @Bean
    public EventSource<Void> PlayHours() {
        return playHours;
    }

    @Bean
    public EventSource<Integer> DeltaStreamSeconds() {
        return deltaStreamSeconds;
    }

    @Bean
    public EventSource<Integer> DeltaStreamMinutes() {
        return deltaStreamMinutes;
    }

    @Bean
    public EventSource<Integer> DeltaStreamHours() {
        return deltaStreamHours;
    }

    @Bean
    public Populator populator() {
        List<EventSource<Integer>> deltaStreams = new ArrayList<>();
        deltaStreams.add(deltaStreamSeconds);
        deltaStreams.add(deltaStreamMinutes);
        deltaStreams.add(deltaStreamHours);
        Populator populator = new Populator(configureClocks(), deltaStreams, seconds, minutes, hours);

        return populator;
    }

    @Bean
    public Ticker ticker() {
        return new Ticker(animator, cache);
    }

    @Bean
    public InMemoryCachedServiceLocator serviceLocator() {
        return cache;
    }

    @Bean
    public Clocks clocks() {
        return configureClocks();
    }

    @Bean
    public UIService uiService() {
        return uiService;
    }

    @Bean
    public DataSource jdbcDataSource() {
        SingleConnectionDataSource ds = new SingleConnectionDataSource();
        ds.setDriverClassName("org.sqlite.JDBC");
        ds.setUrl("jdbc:sqlite:clocks.db");
        return ds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public SavedTimerRepository savedTimerRepository(JdbcTemplate jdbcTemplate) {
        return new SavedTimerSqlliteJdbcRepository(jdbcTemplate);
    }

    @Bean
    BooleanProperty fetchFromDatabase() {
        return new SimpleBooleanProperty(false);
    }

    private Clocks clocks;

    @Bean
    public Clocks configureClocks() {
        if (clocks == null) {
            List<EventSource<Void>> plays = new ArrayList<>();
            plays.add(playMinutes);
            plays.add(playHours);

            List<EventSource<Integer>> deltaStreams = new ArrayList<>();
            deltaStreams.add(deltaStreamSeconds);
            deltaStreams.add(deltaStreamMinutes);
            deltaStreams.add(deltaStreamHours);
            clocks = new Clocks(plays, deltaStreams, seconds, minutes, hours);
        }

        return clocks;
    }
}