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
public class FlywayConfigAsterisk {

    private DataSource asteriskDataSource;


    public FlywayConfigAsterisk(
            @Qualifier("asteriskDataSource") DataSource asteriskDataSource) {
        this.asteriskDataSource = asteriskDataSource;
    }


    @PostConstruct
    public void migrateFlyway() throws SQLException {


/*        Flyway flyway = new Flyway(Flyway.configure()
                .dataSource(voipMonitorDataSource)
                .locations("migration/voipmonitor")
                .baselineOnMigrate(true)
                .table("voipmonitor_schema_history")


        );
        flyway.migrate();*/
        Flyway flyway = new Flyway(Flyway.configure()
                .dataSource(asteriskDataSource)
                .locations("migration/asterisk")
                .baselineOnMigrate(true)
           //     .table("asterisk_schema_history")
        );
        flyway.migrate();
      /*  flyway = Flyway
                .configure()
                .dataSource(awdbDataSource)
                .locations("migration/awdb")
                .baselineOnMigrate(true)
                .table("awdb_schema_history")
                .load();
        flyway.migrate();
        flyway = Flyway
                .configure()
                .dataSource(hdsDataSource)
                .locations("migration/hds")
                .baselineOnMigrate(true)
                .table("hds_schema_history")
                .load();
        flyway.migrate();*/

    }


}
