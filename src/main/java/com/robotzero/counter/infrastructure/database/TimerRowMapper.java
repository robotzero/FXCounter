package com.robotzero.counter.infrastructure.database;

import com.robotzero.counter.entity.Clock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalTime;

public class TimerRowMapper implements org.springframework.jdbc.core.RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        Clock st = new Clock();
        st.setCreated(Instant.parse(rs.getString("created")));
        st.setName(rs.getString("name"));
        st.setSavedTimer(LocalTime.parse(rs.getString("saved_timer")));
        return st;
    }
}
