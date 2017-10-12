package com.robotzero.counter.repository;

import com.robotzero.counter.domain.SavedTimer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalTime;

public class SavedTimerRowMapper implements org.springframework.jdbc.core.RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        SavedTimer st = new SavedTimer();
        st.setCreated(Instant.parse(rs.getString("created")));
        st.setName(rs.getString("name"));
        st.setSavedTimer(LocalTime.parse(rs.getString("saved_timer")));
        return st;
    }
}
