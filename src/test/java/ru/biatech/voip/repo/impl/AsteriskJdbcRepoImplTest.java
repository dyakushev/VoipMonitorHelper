package ru.biatech.voip.repo.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.TestDatabaseAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.biatech.voip.config.DataSourceConfig;
import ru.biatech.voip.config.FlywayConfigAsterisk;
import ru.biatech.voip.model.Extension;
import ru.biatech.voip.repo.AsteriskJdbcRepo;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {FlywayConfigAsterisk.class, DataSourceConfig.class, AsteriskJdbcRepoImpl.class})
class AsteriskJdbcRepoImplTest {

    private AsteriskJdbcRepo asteriskJdbcRepo;
    private static final String APPDATA_100000000 = "100000000",
            APPDATA_100000001 = "100000001",
            exten_50000 = "50000";


    @Autowired
    public void setAsteriskJdbcRepo(@Qualifier("asteriskJdbcRepoImpl") AsteriskJdbcRepo asteriskJdbcRepo) {
        this.asteriskJdbcRepo = asteriskJdbcRepo;
    }

    @Test
    void getExtenByAppdata_OneAsteriskExtensionExists_ReturnsAsteriskExtension() {
        Optional<Extension> extensionOptional = asteriskJdbcRepo.getExtenByAppdata(APPDATA_100000000);
        assertThat(extensionOptional.isPresent()).isEqualTo(true);
        assertThat(extensionOptional.get().getExten()).isEqualTo(exten_50000);
    }
    @Test
    void getExtenByAppdata_AsteriskExtensionDoesNotExist_ReturnsOptionalEmpty() {
        Optional<Extension> extensionOptional = asteriskJdbcRepo.getExtenByAppdata(APPDATA_100000001);
        assertThat(extensionOptional.isEmpty()).isEqualTo(true);
    }

    @Test
    void getExtenByAppdata_MoreThanOneAsteriskExtensionExist_ReturnsOptionalEmpty() {
        Optional<Extension> extensionOptional = asteriskJdbcRepo.getExtenByAppdata(APPDATA_100000001);
        assertThat(extensionOptional.isEmpty()).isEqualTo(true);


    }


}