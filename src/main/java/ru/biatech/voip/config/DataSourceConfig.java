package ru.biatech.voip.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public JdbcTemplate voipMonitorJdbcTemplate() {
        return new JdbcTemplate(voipMonitorDataSource());
    }

    @Bean
    public JdbcTemplate hdsJdbcTemplate() {
        return new JdbcTemplate(hdsDataSource());
    }

    @Bean
    public JdbcTemplate awdbJdbcTemplate() {
        return new JdbcTemplate(awdbDataSource());
    }

    @Bean
    public JdbcTemplate asteriskJdbcTemplate() {
        return new JdbcTemplate(asteriskDataSource());
    }

    @Primary
    @Bean("voipMonitorDatasourceProperties")
    @ConfigurationProperties(prefix = "app.datasource.voipmonitor")
    public DataSourceProperties voipMonitorDatasourceProperties() {
        //DataSourceProperties properties = new DataSourceProperties();
        return new DataSourceProperties();
    }


    @Bean("hdsDatasourceProperties")
    @ConfigurationProperties(prefix = "app.datasource.hds")
    public DataSourceProperties hdsDatasourceProperties() {
        //DataSourceProperties properties = new DataSourceProperties();
        return new DataSourceProperties();
    }

    @Bean("awdbDatasourceProperties")
    @ConfigurationProperties(prefix = "app.datasource.awdb")
    public DataSourceProperties awdbDatasourceProperties() {
        //DataSourceProperties properties = new DataSourceProperties();
        return new DataSourceProperties();
    }

    @Bean("asteriskDatasourceProperties")
    @ConfigurationProperties(prefix = "app.datasource.asterisk")
    public DataSourceProperties asteriskDatasourceProperties() {
        //DataSourceProperties properties = new DataSourceProperties();
        return new DataSourceProperties();
    }

    @Primary
    @Bean("voipMonitorDataSource")
    @ConfigurationProperties("app.datasource.voipmonitor.properties")
    public DataSource voipMonitorDataSource() {
        return voipMonitorDatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }


    @Bean("hdsDataSource")
    @ConfigurationProperties("app.datasource.hds.properties")
    public DataSource hdsDataSource() {
        return hdsDatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean("awdbDataSource")
    @ConfigurationProperties("app.datasource.awdb.properties")
    public DataSource awdbDataSource() {
        return awdbDatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean("asteriskDataSource")
    @ConfigurationProperties("app.datasource.asterisk.properties")
    public DataSource asteriskDataSource() {
        return asteriskDatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }


}
