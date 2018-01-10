package com.robotzero.di;

import com.airhacks.afterburner.views.FXMLView;
import com.robotzero.configuration.SceneConfiguration;
import com.robotzero.counter.clock.ClockPresenter;
import com.robotzero.counter.clock.ClockView;
import com.robotzero.counter.clock.options.OptionsPresenter;
import com.robotzero.counter.clock.options.OptionsView;
import com.robotzero.counter.domain.Direction;
import com.robotzero.counter.domain.clock.Clocks;
import com.robotzero.counter.domain.clock.ClockRepository;
import com.robotzero.counter.infrastructure.database.ClockDatabaseRepository;
import com.robotzero.counter.service.Populator;
import com.robotzero.counter.service.StageController;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.reactfx.EventSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
public class SpringApplicationConfiguration {

    @FXML
    StackPane paneSeconds;

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
    public ClockPresenter clockPresenter(ClockView clockView, Clocks clocks) {
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
    public Subject<Integer> seconds() {
        return BehaviorSubject.create();
    }

    @Bean
    public Subject<Integer> minutes() {
        return BehaviorSubject.create();
    }

    @Bean
    public Subject<Integer> hours() {
        return BehaviorSubject.create();
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
    public Subject<Direction> DeltaStreamSeconds() {
        return BehaviorSubject.create();
    }

    @Bean
    public Subject<Direction> DeltaStreamMinutes() {
        return BehaviorSubject.create();
    }

    @Bean
    public Subject<Direction> DeltaStreamHours() {
        return BehaviorSubject.create();
    }

    @Bean
    public Populator populator(ClockRepository savedTimerRepository) {
        List<Subject<Direction>> deltaStreams = new ArrayList<>();
        deltaStreams.add(DeltaStreamSeconds());
        deltaStreams.add(DeltaStreamMinutes());
        deltaStreams.add(DeltaStreamHours());
        return new Populator(deltaStreams, seconds(), minutes(), hours());
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
    public ClockRepository clockRepository(JdbcTemplate jdbcTemplate) {
        return new ClockDatabaseRepository(jdbcTemplate);
    }

    @Bean
    BooleanProperty fetchFromDatabase() {
        return new SimpleBooleanProperty(false);
    }

    @Bean
    public Clocks configureClocks(ClockRepository clockRepository) {
        List<EventSource<Void>> plays = new ArrayList<>();
        plays.add(PlayMinutes());
        plays.add(PlayHours());
        plays.add(StopCountdown());

        List<Subject<Direction>> deltaStreams = new ArrayList<>();
        deltaStreams.add(DeltaStreamSeconds());
        deltaStreams.add(DeltaStreamMinutes());
        deltaStreams.add(DeltaStreamHours());

        return new Clocks(clockRepository, plays, deltaStreams, seconds(), minutes(), hours());
    }
}