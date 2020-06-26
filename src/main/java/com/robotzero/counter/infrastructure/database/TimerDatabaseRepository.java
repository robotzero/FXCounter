package com.robotzero.counter.infrastructure.database;

import com.robotzero.counter.domain.clock.TimerRepository;
import com.robotzero.counter.entity.Clock;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

public class TimerDatabaseRepository implements TimerRepository {
  private final JdbcTemplate jdbcTemplate;

  public TimerDatabaseRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void create(String name, LocalTime savedTimer) {
    jdbcTemplate.update(
      "INSERT INTO timers (name, saved_timer, created) VALUES (?, ?, ?)",
      name,
      savedTimer.toString(),
      Instant.now().toString()
    );
  }

  @Override
  public Clock selectLatest() {
    List<Clock> result = jdbcTemplate.query("SELECT * FROM timers WHERE created=(SELECT MAX (created) FROM timers)",
        new ClockRowMapper());
    System.out.println(result);
    Clock clock = new Clock();
    clock.setSavedTimer(LocalTime.of(0, 0, 0));
    return clock;
    //        return result.isEmpty() ? null : (Clock) result.get(0);
  }

  @Override
  public void deleteAll() {
    jdbcTemplate.update("DELETE FROM timers");
  }

  private static class ClockRowMapper implements RowMapper<Clock> {

    @Override
    public Clock mapRow(ResultSet rs, int rowNum) throws SQLException {
      ClockResultSetExtractor extractor = new ClockResultSetExtractor();
      return extractor.extractData(rs);
    }
  }

  private static class ClockResultSetExtractor implements ResultSetExtractor<Clock> {

    @Override
    public Clock extractData(ResultSet rs) throws SQLException, DataAccessException {
      Clock clock = new Clock();
      clock.setSavedTimer(LocalTime.of(0, 0, 0));
      return clock;
    }
  }
}
