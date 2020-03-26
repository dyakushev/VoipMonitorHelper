package ru.biatech.voip.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.biatech.voip.model.*;
import ru.biatech.voip.repo.AwdbJdbcRepo;
import ru.biatech.voip.repo.HdsJdbcRepo;
import ru.biatech.voip.repo.VoipMonitorJdbcRepo;
import ru.biatech.voip.service.AgentNameService;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class AgentNameServiceImpl implements AgentNameService {

    private VoipMonitorJdbcRepo voipMonitorJdbcRepo;
    private HdsJdbcRepo hdsJdbcRepo;
    private AwdbJdbcRepo awdbJdbcRepo;

    public AgentNameServiceImpl(@Qualifier("voipMonitorJdbcRepoImpl") VoipMonitorJdbcRepo voipMonitorJdbcRepo,
                                @Qualifier("hdsJdbcRepoImpl") HdsJdbcRepo hdsJdbcRepo,
                                @Qualifier("awdbJdbcRepoImpl") AwdbJdbcRepo awdbJdbcRepo) {
        this.voipMonitorJdbcRepo = voipMonitorJdbcRepo;
        this.hdsJdbcRepo = hdsJdbcRepo;
        this.awdbJdbcRepo = awdbJdbcRepo;
    }

    @Override
    public boolean isCdrSuitable(Cdr cdr) {
        String caller = cdr.getCaller();
        String called = cdr.getCalled();
        if (called == null || caller == null) {
            log.info("Cdr with id {} has empty caller or called", cdr.getId());
            return false;
        }
        //outgoing external call
        if ((caller.length() == 5 && called.length() > 5 && caller.startsWith("5")) ||
                //incoming external call
                (called.length() == 5 && caller.length() > 5 && called.startsWith("5")) ||
                //outgoing internal call CUCM - CUCM
                (called.length() == 5 && caller.length() == 5 && caller.startsWith("5")) ||
                //outgoing internal call asterisk - CUCM
                (called.length() == 5 && caller.length() == 5 && !caller.startsWith("5") && called.startsWith("5"))
        )
            return true;
        //log.info("Cdr is not related to CUCM {}", cdr);
        return false;
    }


    @Override
    /**
     * @deprecated
     * Use getCdrListUntilDate(Timestamp date1) instead
     */
    @Deprecated
    public List<Cdr> getCdrListBetweenDates(Timestamp date1, Timestamp date2) {
        try {
            return voipMonitorJdbcRepo.getCdrsBetweenDates(date1, date2);
        } catch (DataAccessException e) {
            log.error(e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Cdr> getCdrListUntilDate(Timestamp date1) {
        try {
            Long lastCdrId = voipMonitorJdbcRepo.getLastCdrAgentId();
            Long cdrIdUntilDate = voipMonitorJdbcRepo.getMaxIdUntilDate(date1);
            if (lastCdrId != null && cdrIdUntilDate != null)
                if (lastCdrId != 0L && cdrIdUntilDate != 0L)
                    return voipMonitorJdbcRepo.getCdrsByLastIdAndId(lastCdrId, cdrIdUntilDate);
        } catch (DataAccessException e) {
            log.error(e);
        }
        return Collections.emptyList();
    }


    @Override
    public void processCdr(Cdr cdr) {
        getTcdByCdr(cdr)
                .ifPresentOrElse(tcd -> processCdrByTcd(tcd, cdr),
                        () ->
                        {
                            getAgentRealTimeByCdr(cdr)
                                    .ifPresentOrElse(agentRealTime ->
                                                    processCdrByAgentRealTime(agentRealTime, cdr),
                                            () -> {
                                                getAgentLogoutByCdr(cdr)
                                                        .ifPresentOrElse(agentLogout ->
                                                                        processCdrByAgentLogout(agentLogout, cdr),
                                                                () -> log.warn("There is no TCD for cdr {}", cdr));
                                            });
                        }
                );

    }

    private void processCdrByAgentLogout(AgentLogout agentLogout, Cdr cdr) {
        CdrAgent.CdrAgentBuilder builder = CdrAgent.builder().cdrId(cdr.getId());
        getAgentByAgentLogout(agentLogout).ifPresent(agent -> {
                    builder.agentEnterpriseName(agent.getEnterpriseName()).agentId(agent.getPeripheralNumber());
                    getPersonByAgent(agent)
                            .ifPresent(person ->
                                    builder.agentLogin(person.getLoginName())
                            );
                }
        );
        createCdrAgent(builder.build());
    }

    private void processCdrByAgentRealTime(AgentRealTime agentRealTime, Cdr cdr) {
        CdrAgent.CdrAgentBuilder builder = CdrAgent.builder().cdrId(cdr.getId());
        getAgentByAgentRealTime(agentRealTime).ifPresent(agent -> {
                    builder.agentEnterpriseName(agent.getEnterpriseName()).agentId(agent.getPeripheralNumber());
                    getPersonByAgent(agent)
                            .ifPresent(person ->
                                    builder.agentLogin(person.getLoginName())
                            );
                }
        );
        createCdrAgent(builder.build());

    }

    private void processCdrByTcd(Tcd tcd, Cdr cdr) {
        CdrAgent.CdrAgentBuilder builder = CdrAgent.builder().agentId(tcd.getAgentPeripheralNumber());
        builder.cdrId(cdr.getId()).agentId(tcd.getAgentPeripheralNumber());
        //log.info("Agent id={}", tcd.getAgentPeripheralNumber());

        getAgentByTcd(tcd)
                .ifPresent(agent -> {
                            //  log.info("Agent name={}", agent.getEnterpriseName());
                            builder.agentEnterpriseName(agent.getEnterpriseName());
                            getPersonByAgent(agent)
                                    .ifPresent(person ->
                                    {
                                        //    log.info("Agent login name={}", person.getLoginName());
                                        builder.agentLogin(person.getLoginName());
                                    });
                        }
                );
        getCallTypeByTcd(tcd)
                .ifPresent(callType -> {
                    //   log.info("CallType={}", callType.getEnterpriseName());
                    builder.callType(callType.getEnterpriseName());
                });
        getSkillGroupByTcd(tcd)
                .ifPresent(skillGroup ->
                {
                    //   log.info("SkillGroup={}", skillGroup.getEnterpriseName());
                    builder.agentSkill(skillGroup.getEnterpriseName());
                });

        createCdrAgent(builder.build());
    }

    private Optional<Agent> getAgentByAgentLogout(AgentLogout agentLogout) {
        Integer skillTargetId = agentLogout.getSkillTargetId();
        try {
            return awdbJdbcRepo.getAgentBySkillTargetId(skillTargetId);
        } catch (DataAccessException e) {
            log.error(e);
        }
        return Optional.empty();
    }

    private Optional<Agent> getAgentByAgentRealTime(AgentRealTime agentRealTime) {
        Integer skillTargetId = agentRealTime.getSkillTargetId();
        try {
            return awdbJdbcRepo.getAgentBySkillTargetId(skillTargetId);
        } catch (DataAccessException e) {
            log.error(e);
        }
        return Optional.empty();
    }


    private Optional<AgentRealTime> getAgentRealTimeByCdr(Cdr cdr) {
        String caller = cdr.getCaller();
        String called = cdr.getCalled();
        try {
            if (caller.startsWith("5") && caller.length() == 5)
                return awdbJdbcRepo.getAgentRealTimeByExten(caller);
            else if (called.startsWith("5") && called.length() == 5)
                return awdbJdbcRepo.getAgentRealTimeByExten(called);
        } catch (DataAccessException e) {
            log.error(e);
        }
        return Optional.empty();
    }

    private Optional<AgentLogout> getAgentLogoutByCdr(Cdr cdr) {
        String caller = cdr.getCaller();
        String called = cdr.getCalled();
        Timestamp date = cdr.getCallEnd();
        try {
            if (caller.startsWith("5") && caller.length() == 5)
                return awdbJdbcRepo.getAgentLogoutByExten(caller, date);
            else if (called.startsWith("5") && called.length() == 5)
                return awdbJdbcRepo.getAgentLogoutByExten(called, date);
        } catch (DataAccessException e) {
            log.error(e);
        }
        return Optional.empty();
    }


    private Optional<Tcd> getTcdByCdr(Cdr cdr) {
        Long extension = null;
        String caller = cdr.getCaller();
        String called = cdr.getCalled();
        // Timestamp callEndMinus10Seconds = Timestamp.valueOf(cdr.getCallEnd().toLocalDateTime().minusSeconds(20L));
        // Timestamp callEndPlus10Seconds = Timestamp.valueOf(cdr.getCallEnd().toLocalDateTime().plusSeconds(20L));
        Timestamp callEndPlus150Seconds = Timestamp.valueOf(cdr.getCallEnd().toLocalDateTime().plusSeconds(150L));
        Timestamp callEndMinus150Seconds = Timestamp.valueOf(cdr.getCallEnd().toLocalDateTime().minusSeconds(150L));
        try {
        /*    if (caller.length() == 5 && called.length() > 5 && caller.startsWith("5")) {
                extension = Long.valueOf(caller);
                return hdsJdbcRepo.getTcdByDateAndExtention(callEndMinus150Seconds, callEndPlus150Seconds, cdr.getDuration(), extension);
            }
            //incoming external call
            else*/
            if (called.length() == 5 && caller.length() > 5 && called.startsWith("5")) {
                extension = Long.valueOf(called);
                return hdsJdbcRepo.getTcdByDateAndExtentionAndAni(callEndMinus150Seconds, callEndPlus150Seconds, cdr.getDuration(), extension, caller);
            }
            /*//outgoing internal call CUCM - CUCM
            else if (called.length() == 5 && caller.length() == 5 && caller.startsWith("5")) {
                extension = Long.valueOf(caller);
                return hdsJdbcRepo.getTcdByDateAndExtention(callEndMinus150Seconds, callEndPlus150Seconds, cdr.getDuration(), extension);
            }
            //outgoing internal call asterisk - CUCM
            else if (called.length() == 5 && caller.length() == 5 && !caller.startsWith("5") && called.startsWith("5")) {
                extension = Long.valueOf(called);
                return hdsJdbcRepo.getTcdByDateAndExtention(callEndMinus150Seconds, callEndPlus150Seconds, cdr.getDuration(), extension);
            }
            //not agent call*/
            else {
                return Optional.empty();
            }
        }
        //can't parse number
        catch (NumberFormatException e) {
            log.info("Error parsing caller or called number for cdr with id {}", cdr.getId());
        } catch (DataAccessException e) {
            log.error(e);
        }
        return Optional.empty();
    }

    private Optional<Agent> getAgentByTcd(Tcd tcd) {
        String agentPeripheralNumber = tcd.getAgentPeripheralNumber();
        if (agentPeripheralNumber != null)
            if (agentPeripheralNumber != "") {
                try {
                    return awdbJdbcRepo.getAgentByPeripheralNumber(agentPeripheralNumber);
                } catch (DataAccessException e) {
                    log.error(e);
                }
            }
        return Optional.empty();
    }

    private Optional<CallType> getCallTypeByTcd(Tcd tcd) {
        Integer callTypeId = tcd.getCallTypeId();
        if (callTypeId != null)
            if (callTypeId != 0)
                try {
                    return awdbJdbcRepo.getCallTypeById(callTypeId);
                } catch (DataAccessException e) {
                    log.error(e);
                }
        return Optional.empty();
    }

    private Optional<SkillGroup> getSkillGroupByTcd(Tcd tcd) {
        Integer skillGroupId = tcd.getAgentSkillId();
        if (skillGroupId != null)
            if (skillGroupId != 0)
                try {
                    return awdbJdbcRepo.getSkillGroupById(skillGroupId);
                } catch (DataAccessException e) {
                    log.error(e);
                }
        return Optional.empty();
    }

    private Optional<Person> getPersonByAgent(Agent agent) {
        Integer personId = agent.getPersonId();
        if (personId != null)
            if (personId != 0)
                try {
                    return awdbJdbcRepo.getPersonById(personId);
                } catch (DataAccessException e) {
                    log.error(e);
                }
        return Optional.empty();
    }

    private int createCdrAgent(CdrAgent cdrAgent) {
        Long cdrId = cdrAgent.getCdrId();
        String agentLogin = cdrAgent.getAgentLogin();
        if (cdrId != null && agentLogin != null)
            if (cdrId != 0)
                try {
                    return voipMonitorJdbcRepo.insertIntoAgentCdr(cdrAgent.getCdrId(),
                            cdrAgent.getAgentId(),
                            cdrAgent.getAgentLogin(),
                            cdrAgent.getAgentEnterpriseName(),
                            cdrAgent.getAgentSkill(),
                            cdrAgent.getCallType()
                    );
                } catch (DataAccessException e) {
                    log.error(e);
                }
        return 0;
    }
}
