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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

@Configuration
public class SpringApplicationConfiguration {

    private Animator animator = new Animator();
    private InMemoryCachedServiceLocator cache = new InMemoryCachedServiceLocator();
    private FXMLView clockView = new ClockView();
    private UIService uiService = new UIService();

    private Clocks clocks = new Clocks();

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
    public Animator animator() {
        return animator;
    }

    @Bean
    public Populator populator() {
        Populator populator = new Populator(uiService, clocks);

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
        return clocks;
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
}