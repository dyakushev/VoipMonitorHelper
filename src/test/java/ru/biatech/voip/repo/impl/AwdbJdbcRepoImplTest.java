package ru.biatech.voip.repo.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.biatech.voip.config.DataSourceConfig;
import ru.biatech.voip.config.FlywayConfigAwdb;
import ru.biatech.voip.model.*;
import ru.biatech.voip.repo.AwdbJdbcRepo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {FlywayConfigAwdb.class, DataSourceConfig.class, AwdbJdbcRepoImpl.class})
class AwdbJdbcRepoImplTest {
    private AwdbJdbcRepo awdbJdbcRepo;
    private static final Integer SKILL_TARGET_ID_1 = 1,
            SKILL_TARGET_ID_100 = 100,
            SKILL_TARGET_ID_2 = 2,
            PERSON_ID_1 = 1,
            PERSON_ID_100 = 100,
            SKILL_GROUP_SKILL_TARGET_ID_1 = 1,
            SKILL_GROUP_SKILL_TARGET_ID_100 = 100,
            CALL_TYPE_ID_1 = 1,
            CALL_TYPE_ID_100 = 100;
    private static final String ENTERPRISE_NAME_1 = "Testoviy_test1",
            EXTENSION_1001 = "1001",
            EXTENSION_1000 = "1000",
            PERSON_LOGIN_NAME_1 = "ttestoviy1",
            SKILL_GROUP_ENTERPRISE_NAME_1 = "test_skill_1",
            CALL_TYPE_ENTERPRISE_NAME_1 = "test_call_type_1",
            PERIPHERAL_NUMBER_1 = "1",
            PERIPHERAL_NUMBER_100 = "100";

    public AwdbJdbcRepoImplTest(@Qualifier("awdbJdbcRepoImpl") AwdbJdbcRepo awdbJdbcRepo) {
        this.awdbJdbcRepo = awdbJdbcRepo;
    }

    @Test
    void getAgentBySkillTargetId_AgentExists_ReturnsOptionalAgent() {
        Optional<Agent> agentOptional = awdbJdbcRepo.getAgentBySkillTargetId(SKILL_TARGET_ID_1);
        assertThat(agentOptional.isPresent()).isTrue();
        assertThat(agentOptional.get().getEnterpriseName()).isEqualTo(ENTERPRISE_NAME_1);
    }

    @Test
    void getAgentBySkillTargetId_AgentDoesnExist_ReturnsOptionalEmpty() {
        Optional<Agent> agentOptional = awdbJdbcRepo.getAgentBySkillTargetId(SKILL_TARGET_ID_100);
        assertThat(agentOptional.isEmpty()).isTrue();
    }

    @Test
    void getAgentRealTimeByExten_AgentRealTimeExists_ReturnsOptionalAgentRealTime() {
        Optional<AgentRealTime> agentRealTimeOptional = awdbJdbcRepo.getAgentRealTimeByExten(EXTENSION_1001);
        assertThat(agentRealTimeOptional.isPresent()).isTrue();
        assertThat(agentRealTimeOptional.get().getSkillTargetId()).isEqualTo(SKILL_TARGET_ID_2);
    }

    @Test
    void getAgentRealTimeByExten_AgentRealTimeDoesNotExist_ReturnsOptionalEmpty() {
        Optional<AgentRealTime> agentRealTimeOptional = awdbJdbcRepo.getAgentRealTimeByExten(EXTENSION_1000);
        assertThat(agentRealTimeOptional.isEmpty()).isTrue();
    }

    @Test
    void getAgentLogoutByExten_AgentExists_ReturnsOptionalAgentLogout() {
        Optional<AgentLogout> agentLogoutOptional = awdbJdbcRepo.getAgentLogoutByExten(EXTENSION_1000, Timestamp.valueOf(LocalDateTime.now()));
        assertThat(agentLogoutOptional.isPresent()).isTrue();
        assertThat(agentLogoutOptional.get().getSkillTargetId()).isEqualTo(SKILL_TARGET_ID_1);
    }

    @Test
    void getAgentLogoutByExten_AgentDoesNotExist_ReturnsOptionalEmpty() {
        Optional<AgentLogout> agentLogoutOptional = awdbJdbcRepo.getAgentLogoutByExten(EXTENSION_1001, Timestamp.valueOf(LocalDateTime.now()));
        assertThat(agentLogoutOptional.isEmpty()).isTrue();
    }

    @Test
    void getPersonById_PersonExists_ReturnsOptionalPerson() {
        Optional<Person> personOptional = awdbJdbcRepo.getPersonById(PERSON_ID_1);
        assertThat(personOptional.isPresent()).isTrue();
        assertThat(personOptional.get().getLoginName()).isEqualTo(PERSON_LOGIN_NAME_1);
    }

    @Test
    void getPersonById_PersonDoesNotExist_ReturnsOptionalEmpty() {
        Optional<Person> personOptional = awdbJdbcRepo.getPersonById(PERSON_ID_100);
        assertThat(personOptional.isEmpty()).isTrue();
    }


    @Test
    void getSkillGroupById_SkillGroupExists_ReturnsOptionalSkillGroup() {
        Optional<SkillGroup> skillGroupOptional = awdbJdbcRepo.getSkillGroupById(SKILL_GROUP_SKILL_TARGET_ID_1);
        assertThat(skillGroupOptional.isPresent()).isTrue();
        assertThat(skillGroupOptional.get().getEnterpriseName()).isEqualTo(SKILL_GROUP_ENTERPRISE_NAME_1);
    }

    @Test
    void getSkillGroupById_SkillGroupDoesNotExist_ReturnsOptionalEmpty() {
        Optional<SkillGroup> skillGroupOptional = awdbJdbcRepo.getSkillGroupById(SKILL_GROUP_SKILL_TARGET_ID_100);
        assertThat(skillGroupOptional.isEmpty()).isTrue();
    }

    @Test
    void getCallTypeById_CallTypeExists_ReturnsOptionalCallType() {
        Optional<CallType> callTypeOptional = awdbJdbcRepo.getCallTypeById(CALL_TYPE_ID_1);
        assertThat(callTypeOptional.isPresent()).isTrue();
        assertThat(callTypeOptional.get().getEnterpriseName()).isEqualTo(CALL_TYPE_ENTERPRISE_NAME_1);
    }

    @Test
    void getCallTypeById_CallTypeDoesNotExist_ReturnsOptionalEmpty() {
        Optional<CallType> callTypeOptional = awdbJdbcRepo.getCallTypeById(CALL_TYPE_ID_100);
        assertThat(callTypeOptional.isEmpty()).isTrue();
    }

    @Test
    void getAgentByPeripheralNumber_AgentExists_ReturnOptionalAgent() {
        Optional<Agent> optionalAgent = awdbJdbcRepo.getAgentByPeripheralNumber(PERIPHERAL_NUMBER_1);
        assertThat(optionalAgent.isPresent()).isTrue();
        assertThat(optionalAgent.get().getEnterpriseName()).isEqualTo(ENTERPRISE_NAME_1);
    }

    @Test
    void getAgentByPeripheralNumber_AgentDoesNotExist_ReturnOptionalEmpty() {
        Optional<Agent> optionalAgent = awdbJdbcRepo.getAgentByPeripheralNumber(PERIPHERAL_NUMBER_100);
        assertThat(optionalAgent.isEmpty()).isTrue();

    }
}