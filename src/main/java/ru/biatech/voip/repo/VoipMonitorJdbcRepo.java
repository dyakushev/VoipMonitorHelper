package ru.biatech.voip.repo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface VoipMonitorJdbcRepo<T> {
    Optional<T> getCdrById(Long id);

    int updateCdrNextById(Integer id, String agentName);

    int insertIntoAgentCdr(Long cdrId, String agentId, String agentLogin, String agentEnterpriseName, String agentSkill, String callType);

    List<T> getCdrsBetweenDates(Timestamp date1, Timestamp date2);

    List<T> getCdrsByLastIdAndDate(Long id, Timestamp date1);

    List<T> getCdrsByLastIdAndId(Long id1, Long id2);

    Long getMaxIdUntilDate(Timestamp date);

    Long getLastCdrAgentId();

    int updateCdrCallerById(Long id, String exten);

    int updateCdrCalledById(Long id, String exten);
}
