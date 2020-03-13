package ru.biatech.voip.repo.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.biatech.voip.model.Extension;
import ru.biatech.voip.repo.AsteriskJdbcRepo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Log4j2
@Repository
public class AsteriskJdbcRepoImpl implements AsteriskJdbcRepo {
    private JdbcTemplate jdbcTemplate;
    private RowMapper<Extension> extensionRowMapper = new ExtensionRowMapper();

    public AsteriskJdbcRepoImpl(@Qualifier("asteriskJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Extension> getExtenByAppdata(String appdata) {
        appdata = new StringBuilder("\'dialing,s,1,(").append(appdata).append(")\'").toString();
        log.debug("select exten from extensions where appdata={}", appdata);
        String sql = "select exten from extensions where appdata="+appdata;
      /*  String extension = jdbcTemplate.queryForObject(sql, String.class, appdata);
        log.debug(extension);*/
        List<Extension> extensionList = jdbcTemplate.query(sql, extensionRowMapper);
        log.debug(extensionList);
        if (!extensionList.isEmpty())
            if (extensionList.size() >= 1)
                return Optional.of(extensionList.get(0));
        return Optional.empty();
    }

    private class ExtensionRowMapper implements RowMapper<Extension> {
        @Override
        public Extension mapRow(ResultSet resultSet, int i) throws SQLException {
            String exten = resultSet.getString("exten");
            return Extension.builder()
                    .exten(exten)
                    .build();

        }
    }
}
