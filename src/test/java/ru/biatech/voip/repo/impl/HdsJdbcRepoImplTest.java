package ru.biatech.voip.repo.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.biatech.voip.config.DataSourceConfig;
import ru.biatech.voip.config.FlywayConfigHds;
import ru.biatech.voip.model.Tcd;
import ru.biatech.voip.repo.HdsJdbcRepo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {FlywayConfigHds.class, DataSourceConfig.class, HdsJdbcRepoImpl.class})
class HdsJdbcRepoImplTest {
    private HdsJdbcRepo hdsJdbcRepo;
    private static final Timestamp
            dateTimeStart = Timestamp.valueOf(LocalDateTime.now().minusMinutes(2L)),
            dateTimeEnd = Timestamp.valueOf(LocalDateTime.now().plusMinutes(2L));
    private static final Long EXTENSION_1 = 50000L,
            EXTENSION_2 = 50001L;
    private static final String PERIPHERAL_NUMBER_1 = "1",
            ANI_1 = "9999999999";


    public HdsJdbcRepoImplTest(@Qualifier("hdsJdbcRepoImpl") HdsJdbcRepo hdsJdbcRepo) {
        this.hdsJdbcRepo = hdsJdbcRepo;
    }

    @Test
    void getTcdByDateAndExtention_TcdExists_ReturnsOptionalTcd() {
        Optional<Tcd> optionalTcd = hdsJdbcRepo.getTcdByDateAndExtention(dateTimeStart, dateTimeEnd, 0, EXTENSION_1);
        assertThat(optionalTcd.isPresent()).isTrue();
        assertThat(optionalTcd.get().getAgentPeripheralNumber()).isEqualTo(PERIPHERAL_NUMBER_1);
    }

    @Test
    void getTcdByDateAndExtention_TcdDoesNotExist_ReturnsOptionalEmpty() {
        Optional<Tcd> optionalTcd = hdsJdbcRepo.getTcdByDateAndExtention(dateTimeStart, dateTimeEnd, 0, EXTENSION_2);
        assertThat(optionalTcd.isEmpty()).isTrue();
    }

    @Test
    void getTcdByDateAndExtentionAndAni_TcdExists_ReturnsOptionalTcd() {
        Optional<Tcd> optionalTcd = hdsJdbcRepo.getTcdByDateAndExtentionAndAni(dateTimeStart, dateTimeEnd, 0, EXTENSION_1, ANI_1);
        assertThat(optionalTcd.isPresent()).isTrue();
        assertThat(optionalTcd.get().getAgentPeripheralNumber()).isEqualTo(PERIPHERAL_NUMBER_1);
    }

    @Test
    void getTcdByDateAndExtentionAndAni_TcdDoesNotExist_ReturnsOptionalEmpty() {
        Optional<Tcd> optionalTcd = hdsJdbcRepo.getTcdByDateAndExtentionAndAni(dateTimeStart, dateTimeEnd, 0,EXTENSION_2, ANI_1);
        assertThat(optionalTcd.isEmpty()).isTrue();
    }
}