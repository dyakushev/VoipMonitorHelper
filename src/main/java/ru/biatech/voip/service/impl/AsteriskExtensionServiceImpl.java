package ru.biatech.voip.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.biatech.voip.model.Cdr;
import ru.biatech.voip.model.Extension;
import ru.biatech.voip.repo.AsteriskJdbcRepo;
import ru.biatech.voip.repo.VoipMonitorJdbcRepo;
import ru.biatech.voip.service.AsteriskExtensionService;

import java.util.Optional;

@Log4j2
@Service
public class AsteriskExtensionServiceImpl implements AsteriskExtensionService {
    private AsteriskJdbcRepo asteriskJdbcRepo;
    private VoipMonitorJdbcRepo voipMonitorJdbcRepo;

    public AsteriskExtensionServiceImpl(@Qualifier("asteriskJdbcRepoImpl") AsteriskJdbcRepo asteriskJdbcRepo,
                                        @Qualifier("voipMonitorJdbcRepoImpl") VoipMonitorJdbcRepo voipMonitorJdbcRepo
    ) {
        this.asteriskJdbcRepo = asteriskJdbcRepo;
        this.voipMonitorJdbcRepo = voipMonitorJdbcRepo;
    }

    @Override
    public void processCdr(Cdr cdr) {
        String caller = cdr.getCaller();
        String called = cdr.getCalled();
        Long cdrId = cdr.getId();
        if (isExtensionMatch(caller)) {
            modifyCdr(caller, cdrId, "caller");
        }
        if (isExtensionMatch(called)) {
            modifyCdr(called, cdrId, "called");
        }
    }

    @Override
    public boolean isCdrSuitable(Cdr cdr) {
        if (cdr == null)
            return false;
        String called = cdr.getCalled();
        String caller = cdr.getCaller();
        if (called != null && caller != null)
            if (isExtensionMatch(called) || isExtensionMatch(caller))
                return true;
        return false;
    }

    private void modifyCdr(String cdrExten, Long cdrId, String callerOrCalled) {
        Optional<Extension> asteriskExtensionOptional = asteriskJdbcRepo.getExtenByAppdata(cdrExten);
        if (asteriskExtensionOptional.isPresent()) {
            String asteriskExtension = asteriskExtensionOptional.get().getExten();
            //log.info("asterisk extension={}", asteriskExtension);

            try {
                if (callerOrCalled.equals("caller")) {
                    log.info("update asterisk caller for cdr with id {}", cdrId);
                    voipMonitorJdbcRepo.updateCdrCallerById(cdrId, asteriskExtension);
                } else if (callerOrCalled.equals("called")) {
                    log.info("update asterisk called for cdr with id {}", cdrId);
                    voipMonitorJdbcRepo.updateCdrCalledById(cdrId, asteriskExtension);
                } else
                    return;

            } catch (DataAccessException e) {
                log.error(e);
            }
        } else
            log.warn("Asterisk extension not found for {}, where CDR id = {}", cdrExten, cdrId);
    }

    private boolean isExtensionMatch(String exten) {
        if ((exten.startsWith("1") && exten.length() >= 7 && exten.length() <= 10) ||
                (exten.startsWith("2") && exten.length() >= 7 && exten.length() <= 10))
            return true;
        return false;
    }
}
