package com.robotzero.di;

import com.airhacks.afterburner.views.FXMLView;
import com.robotzero.configuration.SceneConfiguration;
import com.robotzero.counter.clock.ClockPresenter;
import com.robotzero.counter.clock.ClockView;
import com.robotzero.counter.clock.options.OptionsPresenter;
import com.robotzero.counter.clock.options.OptionsView;
import com.robotzero.counter.domain.clock.Clock;
import com.robotzero.counter.domain.clock.LocalTimeClock;
import com.robotzero.counter.domain.clock.TimerRepository;
import com.robotzero.counter.infrastructure.database.TimerDatabaseRepository;
import com.robotzero.counter.service.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

@Configuration
public class TimerConfiguration {

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
    public Populator populator() {
        return new Populator();
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
    public TimerRepository clockRepository(JdbcTemplate jdbcTemplate) {
        return new TimerDatabaseRepository(jdbcTemplate);
    }

    @Bean
    BooleanProperty fetchFromDatabase() {
        return new SimpleBooleanProperty(false);
    }

    @Bean
    public Clock configureClocks(TimerRepository clockRepository) {
        return new LocalTimeClock(clockRepository);
    }

    @Bean
    public TimerService timerService() {
        return new TimerService();
    }

    @Bean
    public DirectionService directionService() {
        return new DirectionService();
    }

    @Bean
    public ClockService clockService() {
        return new ClockService(configureClocks(clockRepository(jdbcTemplate(jdbcDataSource()))));
    }

    @Bean
    public ResetService resetService() {
        return new ResetService();
    }
}