package com.robotzero.counter.infrastructure.database;

import com.robotzero.counter.domain.clock.TimerRepository;
import com.robotzero.counter.entity.Clock;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.time.LocalTime;

public class TimerDatabaseRepository implements TimerRepository {

    private final JdbcTemplate jdbcTemplate;

    public TimerDatabaseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(String name, LocalTime savedTimer) {
        jdbcTemplate.update("INSERT INTO timers (name, saved_timer, created) VALUES (?, ?, ?)", name, savedTimer.toString(), Instant.now().toString());
    }

    @Override
    public Clock selectLatest() {
        return null;
//        List result = jdbcTemplate.query("SELECT * FROM timers WHERE created=(SELECT MAX (created) FROM timers)", new ClockRowMapper());
//        return result.isEmpty() ? null : (Clock) result.get(0);
    }

    @Override
    public void deleteAll() {
//        jdbcTemplate.update("DELETE FROM timers");
    }
}
