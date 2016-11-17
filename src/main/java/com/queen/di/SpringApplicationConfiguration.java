package com.queen.di;

import com.airhacks.afterburner.views.FXMLView;
import com.queen.configuration.SceneConfiguration;
import com.queen.counter.clock.ClockPresenter;
import com.queen.counter.clock.ClockView;
import com.queen.counter.clock.options.OptionsPresenter;
import com.queen.counter.domain.Clocks;
import com.queen.counter.clock.options.OptionsView;
import com.queen.counter.repository.SavedTimerRepository;
import com.queen.counter.repository.SavedTimerSqlliteJdbcRepository;
import com.queen.counter.service.Populator;
import com.queen.counter.service.StageController;
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

    @Bean
    public FXMLView clockView() {
        return new ClockView();
    }

    @Bean
    public FXMLView optionsView() { return new OptionsView(); }

    @Bean
    public StageController stageController() {
        return new StageController(sceneConfiguration(), clockView(), optionsView());
    }

    @Bean
    public ClockPresenter clockPresenter() {
        return new ClockPresenter();
    }

    @Bean
    public OptionsPresenter optionPresenter() {
        return new OptionsPresenter();
    }

    @Bean
    public SceneConfiguration sceneConfiguration() {
        return new SceneConfiguration();
    }

    @Bean
    public EventSource seconds() {
        return new EventSource();
    }

    @Bean
    public EventSource minutes() {
        return new EventSource();
    }

    @Bean
    public EventSource hours() {
        return new EventSource();
    }

    @Bean
    public EventSource<Void> PlayMinutes() {
        return new EventSource<>();
    }

    @Bean
    public EventSource<Void> PlayHours() {
        return new EventSource<>();
    }

    @Bean
    public EventSource<Void> StopCountdown() {
        return new EventSource<>();
    }

    @Bean
    public EventSource<Integer> DeltaStreamSeconds() {
        return new EventSource<>();
    }

    @Bean
    public EventSource<Integer> DeltaStreamMinutes() {
        return new EventSource<>();
    }

    @Bean
    public EventSource<Integer> DeltaStreamHours() {
        return new EventSource<>();
    }

    @Bean
    public Populator populator() {
        List<EventSource<Integer>> deltaStreams = new ArrayList<>();
        deltaStreams.add(DeltaStreamSeconds());
        deltaStreams.add(DeltaStreamMinutes());
        deltaStreams.add(DeltaStreamHours());
        Populator populator = new Populator(configureClocks(), deltaStreams, seconds(), minutes(), hours());

        return populator;
    }

    @Bean
    public Clocks clocks() {
        return configureClocks();
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
            plays.add(PlayMinutes());
            plays.add(PlayHours());
            plays.add(StopCountdown());

            List<EventSource<Integer>> deltaStreams = new ArrayList<>();
            deltaStreams.add(DeltaStreamSeconds());
            deltaStreams.add(DeltaStreamMinutes());
            deltaStreams.add(DeltaStreamHours());
            clocks = new Clocks(plays, deltaStreams, seconds(), minutes(), hours());
        }

        return clocks;
    }
}