package ru.biatech.voip.task;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.biatech.voip.model.Cdr;
import ru.biatech.voip.service.AgentNameService;
import ru.biatech.voip.service.AsteriskExtensionService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Log4j2
@EnableAsync
public class AgentNameScheduledTask {
    @Value("${config.CheckCdrBeforeMinute:2}")
    private String minutesBeforeDefault;
    private AgentNameService agentNameService;
    private AsteriskExtensionService asteriskExtensionService;

    public AgentNameScheduledTask(@Qualifier("agentNameServiceImpl") AgentNameService agentNameService,
                                  @Qualifier("asteriskExtensionServiceImpl") AsteriskExtensionService asteriskExtensionService
    ) {
        this.agentNameService = agentNameService;
        this.asteriskExtensionService = asteriskExtensionService;
    }

    //@Async
    @Scheduled(fixedDelayString = "${config.StartTastEveryMiliseconds:60000}")
    public void mainLogic() {
        Long minutesBefore;
        try {
            minutesBefore = Long.parseLong(minutesBeforeDefault);
        } catch (NumberFormatException e) {
            log.warn("Can't parse property config.CheckCdrBeforeMinute = {}", this.minutesBeforeDefault);
            minutesBefore = 2L;
        }
        log.info("Task started at {}", LocalDateTime.now());
        Timestamp minutesBeforeTimestamp = Timestamp.valueOf(LocalDateTime.now().minusMinutes(minutesBefore));
        List<Cdr> cdrList = agentNameService.getCdrListUntilDate(minutesBeforeTimestamp);
        cdrList.stream().forEach(cdr -> {
            if (agentNameService.isCdrSuitable(cdr))
                agentNameService.processCdr(cdr);
            if (asteriskExtensionService.isCdrSuitable(cdr))
                asteriskExtensionService.processCdr(cdr);
        });
        log.info("Task completed at {}, number of cdr proccesed = {}", LocalDateTime.now(), cdrList.size());
    }
}
