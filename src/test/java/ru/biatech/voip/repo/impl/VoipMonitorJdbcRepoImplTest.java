package ru.biatech.voip.repo.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.biatech.voip.config.DataSourceConfig;
import ru.biatech.voip.config.FlywayConfigVoipMonitor;
import ru.biatech.voip.model.Cdr;
import ru.biatech.voip.repo.VoipMonitorJdbcRepo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {FlywayConfigVoipMonitor.class, DataSourceConfig.class, VoipMonitorJdbcRepoImpl.class})
class VoipMonitorJdbcRepoImplTest {
    private VoipMonitorJdbcRepo<Cdr> voipMonitorJdbcRepo;

    public VoipMonitorJdbcRepoImplTest(@Qualifier("voipMonitorJdbcRepoImpl") VoipMonitorJdbcRepo voipMonitorJdbcRepo) {
        this.voipMonitorJdbcRepo = voipMonitorJdbcRepo;
    }

    private static final String TO_EXTENSION_1000 = "1000";
    private static final Long CDR_ID_1 = 1L, CDR_ID_100 = 100L, CDR_ID_0 = 0L;


    @BeforeEach
    void setUp() {
        // toExtension = "1000";

    }

    @Test
    void updateCdrCallerById_CdrExists_CdrUpdatedReturnsOne() {
        int result = voipMonitorJdbcRepo.updateCdrCallerById(CDR_ID_1, TO_EXTENSION_1000);
        assertThat(result).isEqualTo(1);
        String exten = voipMonitorJdbcRepo.getCdrById(CDR_ID_1).get().getCaller();
        assertThat(exten).isEqualTo(TO_EXTENSION_1000);
    }

    @Test
    void updateCdrCallerById_CdrDoesNotExist_ReturnsZero() {
        int result = voipMonitorJdbcRepo.updateCdrCallerById(CDR_ID_100, TO_EXTENSION_1000);
        assertThat(result).isEqualTo(0);
    }

    @Test
    void updateCdrCalledById_CdrExists_CdrUpdatedReturnsOne() {
        int result = voipMonitorJdbcRepo.updateCdrCalledById(CDR_ID_1, TO_EXTENSION_1000);
        assertThat(result).isEqualTo(1);
        String exten = voipMonitorJdbcRepo.getCdrById(CDR_ID_1).get().getCalled();
        assertThat(exten).isEqualTo(TO_EXTENSION_1000);
    }

    @Test
    void updateCdrCalledById_CdrDoesNotExist_ReturnsZero() {
        int result = voipMonitorJdbcRepo.updateCdrCalledById(CDR_ID_100, TO_EXTENSION_1000);
        assertThat(result).isEqualTo(0);
    }

    @Test
    void getCdrsByLastIdAndId_CdrExists_ReturnsNotEmptyCdrList() {
        List<Cdr> cdrList = voipMonitorJdbcRepo.getCdrsByLastIdAndId(CDR_ID_0, CDR_ID_1);
        assertThat(cdrList.isEmpty()).isFalse();
    }

    @Test
    void getCdrsByLastIdAndId_CdrDoesNotExist_ReturnsEmptyCdrList() {
        List<Cdr> cdrList = voipMonitorJdbcRepo.getCdrsByLastIdAndId(CDR_ID_1, CDR_ID_100);
        assertThat(cdrList.isEmpty()).isTrue();
    }

    @Test
    void getMaxIdUntilDate_IdExists_ReturnsId() {
        Long id = voipMonitorJdbcRepo.getMaxIdUntilDate(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        assertThat(id).isEqualTo(CDR_ID_1);
    }

    @Test
    void getMaxIdUntilDate_IdDoesNotExist_ThrowsException() {
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> voipMonitorJdbcRepo.getMaxIdUntilDate(Timestamp.valueOf(LocalDateTime.now())));

    }


    @Test
    void getLastCdrAgentId() {
    }

    @Test
    void insertIntoAgentCdr() {
    }

    @Test
    void getCdrsBetweenDates() {
    }

    @Test
    void getCdrById() {
    }
}