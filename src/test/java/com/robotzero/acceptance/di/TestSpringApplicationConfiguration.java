package com.robotzero.acceptance.di;

import com.robotzero.di.TimerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

@Import(TimerConfiguration.class)
public class TestSpringApplicationConfiguration {

    @Bean
    public DataSource jdbcDataSource() {
        final SingleConnectionDataSource ds = new SingleConnectionDataSource();
        ds.setDriverClassName("org.sqlite.JDBC");
        ds.setUrl("jdbc:sqlite:clocks_test.db");
        return ds;
    }
}
