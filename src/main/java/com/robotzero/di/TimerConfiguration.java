package com.robotzero.di;

import com.airhacks.afterburner.views.FXMLView;
import com.robotzero.configuration.SceneConfiguration;
import com.robotzero.counter.clock.ClockPresenter;
import com.robotzero.counter.clock.ClockView;
import com.robotzero.counter.clock.options.OptionsPresenter;
import com.robotzero.counter.clock.options.OptionsView;
import com.robotzero.counter.domain.CellStateRepository;
import com.robotzero.counter.domain.ColumnType;
import com.robotzero.counter.domain.TimerType;
import com.robotzero.counter.domain.clock.*;
import com.robotzero.counter.infrastructure.database.TimerDatabaseRepository;
import com.robotzero.counter.infrastructure.memory.InMemoryCellStateRepository;
import com.robotzero.counter.infrastructure.memory.InMemoryClockRepository;
import com.robotzero.counter.service.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class TimerConfiguration {

    @Bean
    public FXMLView clockView() {
        return new ClockView();
    }

    @Bean
    public FXMLView optionsView() { return new OptionsView(); }

    @Bean
    public StageController stageController(SceneConfiguration sceneConfiguration, FXMLView clockView, FXMLView optionsView) {
        return new StageController(sceneConfiguration, clockView, optionsView);
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
    public TimerRepository timerRepository(JdbcTemplate jdbcTemplate) {
        return new TimerDatabaseRepository(jdbcTemplate);
    }

    @Bean
    BooleanProperty fetchFromDatabase() {
        return new SimpleBooleanProperty(false);
    }

    @Bean
    public Clock clock(ClockRepository clockRepository, TimerRepository timerRepository, CellStateRepository inMemoryCellStateRepository, LocationService locationService, Map<TimerType, ClockMode> clockModes, DirectionService directionService) {
        return new LocalTimeClock(clockRepository, timerRepository, inMemoryCellStateRepository, locationService, clockModes, directionService);
    }

    @Bean
    public ClockRepository clockRepository() {
        Map<ColumnType, LocalTime> inMemoryClockStateStorage = new HashMap<>();
        inMemoryClockStateStorage.put(ColumnType.MAIN, LocalTime.of(0, 0, 0));
        inMemoryClockStateStorage.put(ColumnType.SECONDS, LocalTime.of(0, 0, 0));
        inMemoryClockStateStorage.put(ColumnType.MINUTES, LocalTime.of(0, 0, 0));
        inMemoryClockStateStorage.put(ColumnType.HOURS, LocalTime.of(0, 0, 0));

        return new InMemoryClockRepository(inMemoryClockStateStorage);
    }

    @Bean
    public Map<TimerType, ClockMode> clockModes(ClockRepository clockRepository, ScrollResetMode scrollResetMode) {
        return Map.of(TimerType.TICK, new TickMode(clockRepository), TimerType.SCROLL, scrollResetMode, TimerType.RESET, scrollResetMode);
    }

    @Bean
    public ScrollResetMode scrollResetMode(ClockRepository clockRepository) {
        return new ScrollResetMode(clockRepository);
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
    public ClockService clockService(Clock clock) {
        return new ClockService(clock);
    }

    @Bean
    public ResetService resetService() {
        return new ResetService();
    }

    @Bean
    public CellStateRepository cellStateRepository() {
        return new InMemoryCellStateRepository();
    }

    @Bean
    public CellService cellService(CellStateRepository cellStateRepository) {
        return new CellService(cellStateRepository);
    }

    @Bean
    public LocationService locationService() {
        return new LocationService();
    }
}