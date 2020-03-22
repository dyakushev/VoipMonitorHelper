package ru.biatech.voip.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

@Profile("test")
@Configuration
public class FlywayConfigHds {
    private DataSource hdsDataSource;

    public FlywayConfigHds(@Qualifier("hdsDataSource") DataSource hdsDataSource) {
        this.hdsDataSource = hdsDataSource;
    }

    @PostConstruct
    public void migrateFlyway() throws SQLException {
        Flyway flyway = new Flyway(Flyway.configure()
                .dataSource(hdsDataSource)
                .locations("migration/hds")
                .baselineOnMigrate(true)
                //  .table("asterisk_schema_history")
        );
        flyway.migrate();
    }

}
