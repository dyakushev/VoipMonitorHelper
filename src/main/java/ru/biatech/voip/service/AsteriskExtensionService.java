package ru.biatech.voip.service;

import ru.biatech.voip.model.Cdr;

public interface AsteriskExtensionService {

    void processCdr(Cdr cdr);

    boolean isCdrSuitable(Cdr cdr);
}
