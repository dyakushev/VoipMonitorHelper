package ru.biatech.voip.repo.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.biatech.voip.model.Tcd;
import ru.biatech.voip.repo.HdsJdbcRepo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@Log4j2
public class HdsJdbcRepoImpl implements HdsJdbcRepo {
    private RowMapper<Tcd> tcdRowMapper = new TcdRowMapper();
    private JdbcTemplate jdbcTemplate;

    public HdsJdbcRepoImpl(@Qualifier("hdsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Tcd> getTcdByDateAndExtention(Timestamp callDate, Timestamp callEnd, Integer duration, Long extension) {
        String sql = "select AgentPeripheralNumber,SkillGroupSkillTargetID,CallTypeID from t_Termination_Call_Detail where DateTime between ? and ? and InstrumentPortNumber=?";
        Object[] args = {callDate, callEnd, extension};
        List<Tcd> tcdList = jdbcTemplate.query(sql, tcdRowMapper, args);
        if (!tcdList.isEmpty())
            if (tcdList.size() == 1)
                return Optional.of(tcdList.get(0));
        return Optional.empty();
    }

    @Override
    public Optional<Tcd> getTcdByDateAndExtentionAndAni(Timestamp callDate, Timestamp callEnd, Integer duration, Long extension, String ani) {
        String sql = "select AgentPeripheralNumber,SkillGroupSkillTargetID,CallTypeID from t_Termination_Call_Detail where DateTime between ? and ? and InstrumentPortNumber=? and ANI=?";
        Object[] args = {callDate, callEnd, extension, ani};

        List<Tcd> tcdList = jdbcTemplate.query(sql, tcdRowMapper, args);
        if (!tcdList.isEmpty())
            if (tcdList.size() == 1)
                return Optional.of(tcdList.get(0));
        return Optional.empty();
    }


    private class TcdRowMapper implements RowMapper<Tcd> {
        @Override
        public Tcd mapRow(ResultSet resultSet, int i) throws SQLException {
            String agentPeripheralNumber = resultSet.getString("AgentPeripheralNumber");
            Integer agentSkillId = resultSet.getInt("SkillGroupSkillTargetID");
            Integer callTypeId = resultSet.getInt("CallTypeID");

            return Tcd
                    .builder()
                    .agentPeripheralNumber(agentPeripheralNumber)
                    .agentSkillId(agentSkillId)
                    .callTypeId(callTypeId)
                    .build();
        }
    }

}
