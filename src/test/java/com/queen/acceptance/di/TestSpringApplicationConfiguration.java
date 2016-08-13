package com.queen.acceptance.di;

import com.queen.di.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

public class TestSpringApplicationConfiguration extends SpringApplicationConfiguration {

    @Bean
    public DataSource jdbcDataSource() {
        SingleConnectionDataSource ds = new SingleConnectionDataSource();
        ds.setDriverClassName("org.sqlite.JDBC");
        ds.setUrl("jdbc:sqlite:clocks_test.db");
        return ds;
    }
}
