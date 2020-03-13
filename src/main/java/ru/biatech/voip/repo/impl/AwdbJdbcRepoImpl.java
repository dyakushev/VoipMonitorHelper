package ru.biatech.voip.repo.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.biatech.voip.model.*;
import ru.biatech.voip.repo.AwdbJdbcRepo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@Log4j2
public class AwdbJdbcRepoImpl implements AwdbJdbcRepo {
    private RowMapper<Agent> agentRowMapper = new AgentRowMapper();
    private RowMapper<SkillGroup> skillGroupRowMapper = new SkillGroupRowMapper();
    private RowMapper<CallType> callTypeRowMapper = new CallTypeRowMapper();
    private RowMapper<Person> personRowMapper = new PersonRowMapper();
    private RowMapper<AgentRealTime> agentRealTimeRowMapper = new AgentRealTimeRowMapper();
    private RowMapper<AgentLogout> agentLogoutRowMapper = new AgentLogoutRowMapper();

    private JdbcTemplate jdbcTemplate;

    public AwdbJdbcRepoImpl(@Qualifier("awdbJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Agent> getAgentBySkillTargetId(Integer skillTargetId) {
        if (skillTargetId == null)
            return Optional.empty();
        if (skillTargetId == 0)
            return Optional.empty();
        String sql = "select EnterpriseName,PersonID, PeripheralNumber from t_Agent where SkillTargetID=?";
        List<Agent> agentList = jdbcTemplate.query(sql, agentRowMapper, skillTargetId);

        if (!agentList.isEmpty())
            if (agentList.size() == 1)
                return Optional.of(agentList.get(0));
        return Optional.empty();
    }

    @Override
    public Optional<AgentRealTime> getAgentRealTimeByExten(String exten) {
        String sql = "select SkillTargetID, SkillGroupSkillTargetID from t_Agent_Real_Time where Extension=?";
        List<AgentRealTime> agentRealTimeList = jdbcTemplate.query(sql, agentRealTimeRowMapper, exten);
        if (!agentRealTimeList.isEmpty())
            if (agentRealTimeList.size() == 1)
                return Optional.of(agentRealTimeList.get(0));
        return Optional.empty();
    }

    @Override
    public Optional<AgentLogout> getAgentLogoutByExten(String exten, Timestamp date) {
        String sql = "SELECT TOP 1 SkillTargetID from t_Agent_Logout where Extension =? and LogoutDateTime < ? order by LogoutDateTime desc";
        List<AgentLogout> agentLogoutList = jdbcTemplate.query(sql, agentLogoutRowMapper, exten, date);
        if (!agentLogoutList.isEmpty())
            if (agentLogoutList.size() == 1)
                return Optional.of(agentLogoutList.get(0));
        return Optional.empty();
    }

    @Override
    public Optional<Person> getPersonById(Integer id) {
        if (id == null)
            return Optional.empty();
        if (id == 0)
            return Optional.empty();
        String sql = "select LoginName from t_Person where PersonID=?";
        List<Person> personList = jdbcTemplate.query(sql, personRowMapper, id);
        if (!personList.isEmpty())
            if (personList.size() == 1)
                return Optional.of(personList.get(0));
        return Optional.empty();
    }

    @Override
    public Optional<SkillGroup> getSkillGroupById(Integer id) {
        if (id == null)
            return Optional.empty();
        if (id == 0)
            return Optional.empty();
        String sql = "select EnterpriseName from t_Skill_Group where SkillTargetID=?";
        List<SkillGroup> skillGroupList = jdbcTemplate.query(sql, skillGroupRowMapper, id);

        if (!skillGroupList.isEmpty())
            if (skillGroupList.size() == 1)
                return Optional.of(skillGroupList.get(0));
        return Optional.empty();
    }

    @Override
    public Optional<CallType> getCallTypeById(Integer id) {
        if (id == null)
            return Optional.empty();
        if (id == 0)
            return Optional.empty();
        String sql = "select EnterpriseName from t_Call_Type where CallTypeID=?";
        List<CallType> callTypeList = jdbcTemplate.query(sql, callTypeRowMapper, id);

        if (!callTypeList.isEmpty())
            if (callTypeList.size() == 1)
                return Optional.of(callTypeList.get(0));
        return Optional.empty();
    }

    @Override
    public Optional<Agent> getAgentByPeripheralNumber(String peripheralNumber) {
        if (peripheralNumber == null)
            return Optional.empty();
        if (peripheralNumber == "")
            return Optional.empty();
        String sql = "select EnterpriseName,PersonID,PeripheralNumber from t_Agent where PeripheralNumber=?";
        List<Agent> agentList = jdbcTemplate.query(sql, agentRowMapper, peripheralNumber);

        if (!agentList.isEmpty())
            if (agentList.size() == 1)
                return Optional.of(agentList.get(0));
        return Optional.empty();
    }

    private class AgentRowMapper implements RowMapper<Agent> {
        @Override
        public Agent mapRow(ResultSet resultSet, int i) throws SQLException {
            String enterpriseName = resultSet.getString("EnterpriseName");
            Integer personId = resultSet.getInt("PersonID");
            String peripheralNumber = resultSet.getString("PeripheralNumber");
            return Agent
                    .builder()
                    .enterpriseName(enterpriseName)
                    .personId(personId)
                    .peripheralNumber(peripheralNumber)
                    .build();
        }
    }

    private class SkillGroupRowMapper implements RowMapper<SkillGroup> {
        @Override
        public SkillGroup mapRow(ResultSet resultSet, int i) throws SQLException {
            String enterpriseName = resultSet.getString("EnterpriseName");
            return SkillGroup
                    .builder()
                    .enterpriseName(enterpriseName)
                    .build();
        }
    }

    private class CallTypeRowMapper implements RowMapper<CallType> {
        @Override
        public CallType mapRow(ResultSet resultSet, int i) throws SQLException {
            String enterpriseName = resultSet.getString("EnterpriseName");
            return CallType
                    .builder()
                    .enterpriseName(enterpriseName)
                    .build();
        }
    }

    private class PersonRowMapper implements RowMapper<Person> {
        @Override
        public Person mapRow(ResultSet resultSet, int i) throws SQLException {
            String loginName = resultSet.getString("LoginName");
            return Person
                    .builder()
                    .loginName(loginName)
                    .build();
        }
    }

    private class AgentRealTimeRowMapper implements RowMapper<AgentRealTime> {
        @Override
        public AgentRealTime mapRow(ResultSet resultSet, int i) throws SQLException {
            Integer skillTargetId = resultSet.getInt("SkillTargetID");
            Integer skillGroupSkillTargetId = resultSet.getInt("SkillGroupSkillTargetID");
            return AgentRealTime
                    .builder()
                    .skillTargetId(skillTargetId)
                    .skillGroupSkillTargetID(skillGroupSkillTargetId)
                    .build();

        }
    }

    private class AgentLogoutRowMapper implements RowMapper<AgentLogout> {
        @Override
        public AgentLogout mapRow(ResultSet resultSet, int i) throws SQLException {
            Integer skillTargetId = resultSet.getInt("SkillTargetID");
            return AgentLogout
                    .builder()
                    .skillTargetId(skillTargetId)
                    .build();
        }
    }


}
