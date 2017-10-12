package com.robotzero.counter.repository;

import com.robotzero.counter.domain.SavedTimer;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

public class SavedTimerSqlliteJdbcRepository implements SavedTimerRepository {

    private JdbcTemplate jdbcTemplate;

    public SavedTimerSqlliteJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(String name, LocalTime savedTimer) {
        jdbcTemplate.update("INSERT INTO timers (name, saved_timer, created) VALUES (?, ?, ?)", name, savedTimer.toString(), Instant.now().toString());
    }

    @Override
    public SavedTimer selectLatest() {
        List result = jdbcTemplate.query("SELECT * FROM timers WHERE created=(SELECT MAX (created) FROM timers)", new SavedTimerRowMapper());
        return result.isEmpty() ? null : (SavedTimer) result.get(0);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM timers");
    }
}
