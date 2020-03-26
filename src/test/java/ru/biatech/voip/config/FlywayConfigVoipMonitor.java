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
public class FlywayConfigVoipMonitor {
    private DataSource voipMonitorDataSource;

    public FlywayConfigVoipMonitor(@Qualifier("voipMonitorDataSource") DataSource voipMonitorDataSource) {
        this.voipMonitorDataSource = voipMonitorDataSource;
    }

    @PostConstruct
    public void migrateFlyway() throws SQLException {
        Flyway flyway = new Flyway(Flyway.configure()
                .dataSource(voipMonitorDataSource)
                .locations("migration/voipmonitor")
                .baselineOnMigrate(true)
                //  .table("asterisk_schema_history")
        );
        flyway.migrate();
    }

}
