package ru.biatech.voip.repo.impl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.biatech.voip.config.DataSourceConfig;
import ru.biatech.voip.config.FlywayConfigVoipMonitor;
import ru.biatech.voip.model.Cdr;
import ru.biatech.voip.repo.VoipMonitorJdbcRepo;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {FlywayConfigVoipMonitor.class, DataSourceConfig.class, VoipMonitorJdbcRepoImpl.class})
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VoipMonitorJdbcRepoImplTest {
    private VoipMonitorJdbcRepo<Cdr> voipMonitorJdbcRepo;
    private DataSource voipMonitorDataSource;
    private JdbcTemplate voipMonitorJdbcTemplate;
    private static final String TO_EXTENSION_1000 = "1000";
    private static final Long CDR_ID_1 = 1L, CDR_ID_100 = 100L, CDR_ID_0 = 0L;

    public VoipMonitorJdbcRepoImplTest(@Qualifier("voipMonitorJdbcRepoImpl") VoipMonitorJdbcRepo voipMonitorJdbcRepo, @Qualifier("voipMonitorDataSource") DataSource voipMonitorDataSource, @Qualifier("voipMonitorJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.voipMonitorJdbcRepo = voipMonitorJdbcRepo;
        this.voipMonitorDataSource = voipMonitorDataSource;
        this.voipMonitorJdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    public void setUp() {

        voipMonitorJdbcTemplate.batchUpdate(
                "delete from cdr_agent",
                "insert into cdr_agent (cdr_id, agent_id, agent_login, agent_enterprise_name, agent_skill, call_type) values (1, '1', 'testoviy', 'Test_Testoviy', null, null)",
                "delete from cdr",
                "insert into cdr (ID, calldate, callend, duration, caller, called) values (1, NOW(), DATEADD(MINUTE, 1, NOW()), 60, '1000000000', '89999999999')");

    }

    @Test
    @Order(1)
    void updateCdrCallerById_CdrExists_CdrUpdatedReturnsOne() {
        //when
        int result = voipMonitorJdbcRepo.updateCdrCallerById(CDR_ID_1, TO_EXTENSION_1000);
        //then
        assertThat(result).isEqualTo(1);
        //when
        String exten = voipMonitorJdbcRepo.getCdrById(CDR_ID_1).get().getCaller();
        //then
        assertThat(exten).isEqualTo(TO_EXTENSION_1000);
    }

    @Test
    @Order(2)
    void updateCdrCallerById_CdrDoesNotExist_ReturnsZero() {
        //when
        int result = voipMonitorJdbcRepo.updateCdrCallerById(CDR_ID_100, TO_EXTENSION_1000);
        //then
        assertThat(result).isEqualTo(0);
    }

    @Test
    @Order(3)
    void updateCdrCalledById_CdrExists_CdrUpdatedReturnsOne() {
        //when
        int result = voipMonitorJdbcRepo.updateCdrCalledById(CDR_ID_1, TO_EXTENSION_1000);
        //then
        assertThat(result).isEqualTo(1);
        //when
        String exten = voipMonitorJdbcRepo.getCdrById(CDR_ID_1).get().getCalled();
        //then
        assertThat(exten).isEqualTo(TO_EXTENSION_1000);
    }

    @Test
    @Order(4)
    void updateCdrCalledById_CdrDoesNotExist_ReturnsZero() {
        //when
        int result = voipMonitorJdbcRepo.updateCdrCalledById(CDR_ID_100, TO_EXTENSION_1000);
        //then
        assertThat(result).isEqualTo(0);
    }

    @Test
    @Order(5)
    void getCdrsByLastIdAndId_CdrExists_ReturnsNotEmptyCdrList() {
        //when
        List<Cdr> cdrList = voipMonitorJdbcRepo.getCdrsByLastIdAndId(CDR_ID_0, CDR_ID_1);
        //then
        assertThat(cdrList.isEmpty()).isFalse();
    }

    @Test
    @Order(6)
    void getCdrsByLastIdAndId_CdrDoesNotExist_ReturnsEmptyCdrList() {
        //when
        List<Cdr> cdrList = voipMonitorJdbcRepo.getCdrsByLastIdAndId(CDR_ID_1, CDR_ID_100);
        //then
        assertThat(cdrList.isEmpty()).isTrue();
    }

    @Test
    @Order(7)
    void getMaxIdUntilDate_IdExists_ReturnsId() {
        //when
        Long id = voipMonitorJdbcRepo.getMaxIdUntilDate(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        //then
        assertThat(id).isEqualTo(CDR_ID_1);
    }

    @Order(8)
    @Test
    void getMaxIdUntilDate_IdDoesNotExist_ThrowsException() {
        //then
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> voipMonitorJdbcRepo.getMaxIdUntilDate(Timestamp.valueOf(LocalDateTime.now())));

    }

    @Test
    @Order(9)
    void getLastCdrAgentId_IdExists_ReturnsId() {
        //when
        Long id = voipMonitorJdbcRepo.getLastCdrAgentId();
        //then
        assertThat(id).isEqualTo(CDR_ID_1);
    }

    @Test
    @Order(10)
    void getLastCdrAgentId_IdDoesNotExist_ThrowsException() {
        //when
        voipMonitorJdbcTemplate.update("delete from cdr_agent");
        //then
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> voipMonitorJdbcRepo.getLastCdrAgentId());
    }


    @Test
    @Order(10)
    void insertIntoAgentCdr_IdDoesNotExist_ReturnsOne() {
        //when
        int result = voipMonitorJdbcRepo.insertIntoAgentCdr(100L, "100", "agent100", null, null, null);
        //then
        assertThat(result).isEqualTo(1);
        //when
        Long id = voipMonitorJdbcRepo.getLastCdrAgentId();
        //then
        assertThat(id).isEqualTo(CDR_ID_100);
    }

    @Test
    @Order(11)
    void insertIntoAgentCdr_IdExists_ThrowsException() {
        //then
        Assertions.assertThrows(DuplicateKeyException.class, () -> voipMonitorJdbcRepo.insertIntoAgentCdr(1L, "1", "testoviy", "Test_Testoviy", null, null));

    }

    @Test
    @Order(12)
    void getCdrsBetweenDates_CdrsExist_ReturnsNonEmptyList() {
        //given
        Timestamp date1 = Timestamp.valueOf(LocalDateTime.now().minusMinutes(10L)), date2 = Timestamp.valueOf(LocalDateTime.now().plusMinutes(10L));
        //when
        List<Cdr> cdrList = voipMonitorJdbcRepo.getCdrsBetweenDates(date1, date2);
        //then
        assertThat(cdrList.size()).isGreaterThan(0);
    }

    @Test
    @Order(13)
    void getCdrsBetweenDates_CdrsDoNotExist_ReturnsEmptyList() {
        //given
        Timestamp date1 = Timestamp.valueOf(LocalDateTime.now().minusMinutes(60L)), date2 = Timestamp.valueOf(LocalDateTime.now().minusMinutes(40L));
        //when
        List<Cdr> cdrList = voipMonitorJdbcRepo.getCdrsBetweenDates(date1, date2);
        //then
        assertThat(cdrList.size()).isEqualTo(0);

    }


    @Test
    @Order(15)
    void getCdrById_CdrExists_ReturnsOptionalCdr() {
        //when
        Optional<Cdr> cdrOptional = voipMonitorJdbcRepo.getCdrById(1L);
        //then
        assertThat(cdrOptional.isPresent()).isTrue();
    }

    @Test
    @Order(16)
    void getCdrById_CdrDoesNotExist_ReturnsOptionalEmpty() {
        //when
        Optional<Cdr> cdrOptional = voipMonitorJdbcRepo.getCdrById(100L);
        //then
        assertThat(cdrOptional.isEmpty()).isTrue();
    }


}