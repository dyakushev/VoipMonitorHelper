package ru.biatech.voip.repo;

import ru.biatech.voip.model.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Optional;

public interface AwdbJdbcRepo {
    Optional<Agent> getAgentByPeripheralNumber(String peripheralNumber);

    Optional<Agent> getAgentBySkillTargetId(Integer skillTargetId);

    Optional<SkillGroup> getSkillGroupById(Integer id);

    Optional<CallType> getCallTypeById(Integer id);

    Optional<Person> getPersonById(Integer id);

    Optional<AgentRealTime> getAgentRealTimeByExten(String exten);

    Optional<AgentLogout> getAgentLogoutByExten(String exten, Timestamp date);

    //String getPeripheralNumberBySkillTargetId
}
