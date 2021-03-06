package com.robotzero.di;

import com.robotzero.counter.clock.ClockController;
import com.robotzero.counter.domain.CellStateRepository;
import com.robotzero.counter.domain.TimerType;
import com.robotzero.counter.domain.clock.ChangeableState;
import com.robotzero.counter.domain.clock.Clock;
import com.robotzero.counter.domain.clock.ClockMode;
import com.robotzero.counter.domain.clock.ClockRepository;
import com.robotzero.counter.domain.clock.LocalTimeClock;
import com.robotzero.counter.domain.clock.ScrollResetMode;
import com.robotzero.counter.domain.clock.TickMode;
import com.robotzero.counter.domain.clock.TimerRepository;
import com.robotzero.counter.infrastructure.database.TimerDatabaseRepository;
import com.robotzero.counter.infrastructure.memory.InMemoryCellStateRepository;
import com.robotzero.counter.infrastructure.memory.InMemoryClockRepository;
import com.robotzero.counter.service.CellService;
import com.robotzero.counter.service.ClockService;
import com.robotzero.counter.service.DirectionService;
import com.robotzero.counter.service.LocationService;
import com.robotzero.counter.service.ResetService;
import com.robotzero.counter.service.TimerService;
import java.util.List;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

@Configuration
public class TimerConfiguration {

  @Bean
  public ClockController clockPresenter(
    final TimerService timerService,
    final CellService cellService,
    final ClockService clockService,
    final ResetService resetService
  ) {
    return new ClockController(timerService, cellService, clockService, resetService);
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
  public Clock clock(
    final ClockRepository clockRepository,
    final TimerRepository timerRepository,
    final CellStateRepository inMemoryCellStateRepository,
    final LocationService locationService,
    final Map<TimerType, ClockMode> clockModes,
    final DirectionService directionService,
    final List<ChangeableState> changeableStates
  ) {
    return new LocalTimeClock(
      clockRepository,
      timerRepository,
      inMemoryCellStateRepository,
      locationService,
      clockModes,
      directionService,
      changeableStates
    );
  }

  @Bean
  public ClockRepository clockRepository() {
    return new InMemoryClockRepository();
  }

  @Bean
  public Map<TimerType, ClockMode> clockModes(
    final ClockRepository clockRepository,
    final ScrollResetMode scrollResetMode
  ) {
    return Map.of(
      TimerType.TICK,
      new TickMode(clockRepository),
      TimerType.SCROLL,
      scrollResetMode,
      TimerType.RESET,
      scrollResetMode
    );
  }

  @Bean
  public List<ChangeableState> changeableStates() {
    return List.of(
      ChangeableState.FIRSTBOTTOM,
      ChangeableState.FIRSTTOP,
      ChangeableState.LASTBOTTOM,
      ChangeableState.LASTTOP
    );
  }

  @Bean
  public ScrollResetMode scrollResetMode(final ClockRepository clockRepository) {
    return new ScrollResetMode(clockRepository);
  }

  @Bean
  public TimerService timerService(final TimerRepository timerRepository, final ClockRepository clockRepository) {
    return new TimerService(timerRepository, clockRepository);
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
