package com.robotzero.di;

import com.airhacks.afterburner.views.FXMLView;
import com.robotzero.configuration.SceneConfiguration;
import com.robotzero.counter.clock.ClockPresenter;
import com.robotzero.counter.clock.ClockView;
import com.robotzero.counter.clock.options.OptionsPresenter;
import com.robotzero.counter.clock.options.OptionsView;
import com.robotzero.counter.domain.CellStateRepository;
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
import java.util.List;
import java.util.Map;

@Configuration
public class TimerConfiguration {

    @Bean
    public FXMLView clockView() {
        return new ClockView();
    }

    @Bean
    public StageController stageController(final SceneConfiguration sceneConfiguration, final FXMLView clockView) {
        return new StageController(sceneConfiguration, clockView);
    }

    @Bean
    public ClockPresenter clockPresenter(final Populator populator, final TimerService timerService, final CellService cellService, final ClockService clockService, final ResetService resetService) {
        return new ClockPresenter(populator, timerService, cellService, clockService, resetService);
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
    public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public TimerRepository timerRepository(final JdbcTemplate jdbcTemplate) {
        return new TimerDatabaseRepository(jdbcTemplate);
    }

    @Bean
    BooleanProperty fetchFromDatabase() {
        return new SimpleBooleanProperty(false);
    }

    @Bean
    public Clock clock(final ClockRepository clockRepository, final TimerRepository timerRepository, final CellStateRepository inMemoryCellStateRepository, final LocationService locationService, final Map<TimerType, ClockMode> clockModes, final DirectionService directionService, final List<ChangeableState> changeableStates) {
        return new LocalTimeClock(clockRepository, timerRepository, inMemoryCellStateRepository, locationService, clockModes, directionService, changeableStates);
    }

    @Bean
    public ClockRepository clockRepository() {
        return new InMemoryClockRepository();
    }

    @Bean
    public Map<TimerType, ClockMode> clockModes(final ClockRepository clockRepository, final ScrollResetMode scrollResetMode) {
        return Map.of(TimerType.TICK, new TickMode(clockRepository), TimerType.SCROLL, scrollResetMode, TimerType.RESET, scrollResetMode);
    }

    @Bean
    public List<ChangeableState> changeableStates() {
        return List.of(ChangeableState.FIRSTBOTTOM, ChangeableState.FIRSTTOP, ChangeableState.LASTBOTTOM, ChangeableState.LASTTOP);
    }

    @Bean
    public ScrollResetMode scrollResetMode(final ClockRepository clockRepository) {
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
    public ClockService clockService(final Clock clock) {
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
    public CellService cellService(final CellStateRepository cellStateRepository) {
        return new CellService(cellStateRepository);
    }

    @Bean
    public LocationService locationService() {
        return new LocationService();
    }
}