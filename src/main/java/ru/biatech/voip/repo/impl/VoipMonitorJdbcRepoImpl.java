package ru.biatech.voip.repo.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.biatech.voip.model.Cdr;
import ru.biatech.voip.model.CdrNext;
import ru.biatech.voip.repo.VoipMonitorJdbcRepo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class VoipMonitorJdbcRepoImpl implements VoipMonitorJdbcRepo<Cdr> {
    private RowMapper<Cdr> cdrRowMapper = new CdrRowMapper();
    private RowMapper<CdrNext> cdrNextRowMapper = new CdrNextRowMapper();
    private JdbcTemplate jdbcTemplate;

    public VoipMonitorJdbcRepoImpl(@Qualifier("voipMonitorJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int updateCdrCallerById(Long id, String exten) {
        String sql = "update cdr set caller=? where ID=?";
        return jdbcTemplate.update(sql, exten, id);
    }

    @Override
    public int updateCdrCalledById(Long id, String exten) {
        String sql = "update cdr set called=? where ID=?";
        return jdbcTemplate.update(sql, exten, id);
    }

    @Override
    public List<Cdr> getCdrsByLastIdAndId(Long id1, Long id2) {
        String sql = "select * from cdr where ID>? and ID<=?";
        return jdbcTemplate.query(sql, cdrRowMapper, id1, id2);
    }

    @Override
    public Long getMaxIdUntilDate(Timestamp date) {
        String sql = "SELECT ID FROM cdr where callend<=? ORDER BY ID DESC LIMIT 1;";
        return jdbcTemplate.queryForObject(sql, Long.class, date);
    }

    @Override
    public List<Cdr> getCdrsByLastIdAndDate(Long id, Timestamp date1) {
        String sql = "select * from cdr where ID>? and Date(callend)<?";
        return jdbcTemplate.query(sql, cdrRowMapper, id, date1);
    }

    @Override
    public Long getLastCdrAgentId() {
        String sql = "SELECT cdr_id FROM cdr_agent ORDER BY cdr_id DESC LIMIT 1;";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    @Override
    public int insertIntoAgentCdr(Long cdrId, String agentId, String agentLogin, String agentEnterpriseName, String agentSkill, String callType) {
        String sql = "insert into cdr_agent (cdr_id,agent_id,agent_login,agent_enterprise_name,agent_skill,call_type) values (?,?,?,?,?,?)";
        Object[] args = {cdrId, agentId, agentLogin, agentEnterpriseName, agentSkill, callType};
        return jdbcTemplate.update(sql, args);
    }

    @Override
    public List<Cdr> getCdrsBetweenDates(Timestamp date1, Timestamp date2) {
        String sql = "select ID,calldate,callend,duration,caller,called  from cdr where callend between ? and ?";
        return jdbcTemplate.query(sql, cdrRowMapper, date1, date2);
    }

    @Override
    public Optional<Cdr> getCdrById(Long id) {
        String sql = "select ID,calldate,callend,duration,caller,called  from cdr where id=?";
        List<Cdr> cdrList = jdbcTemplate.query(sql, cdrRowMapper, id);
        if (!cdrList.isEmpty()) {
            if (cdrList.size() == 1)
                return Optional.of(cdrList.get(0));
        }
        return Optional.empty();
    }


    @Override
    public int updateCdrNextById(Integer id, String agentName) {
        String selectSql = "select custom_header1 from cdr_next where cdr_ID=?";
        List<CdrNext> cdrNextList = jdbcTemplate.query(selectSql, cdrNextRowMapper, id);
        if (!cdrNextList.isEmpty())
            if (cdrNextList.size() == 1) {
                String updateSql = "update cdr_next set custom_header1=? where cdr_ID=?";
                return jdbcTemplate.update(updateSql, agentName, id);
            }
        return 0;
    }

    private class CdrRowMapper implements RowMapper<Cdr> {
        @Override
        public Cdr mapRow(ResultSet resultSet, int i) throws SQLException {
            Long id = resultSet.getLong("ID");
            Timestamp callDate = resultSet.getTimestamp("calldate");
            Timestamp callEnd = resultSet.getTimestamp("callend");
            Integer duration = resultSet.getInt("duration");
            String caller = resultSet.getString("caller");
            String called = resultSet.getString("called");
            return Cdr.builder()
                    .Id(id)
                    .callDate(callDate)
                    .callEnd(callEnd)
                    .called(called)
                    .duration(duration)
                    .caller(caller)
                    .build();
        }
    }

    private class CdrNextRowMapper implements RowMapper<CdrNext> {
        @Override
        public CdrNext mapRow(ResultSet resultSet, int i) throws SQLException {
            Long id = resultSet.getLong("cdr_ID");
            String agentName = resultSet.getString("custom_header1");
            return CdrNext
                    .builder()
                    .cdrId(id)
                    .agentName(agentName)
                    .build();
        }
    }


}
