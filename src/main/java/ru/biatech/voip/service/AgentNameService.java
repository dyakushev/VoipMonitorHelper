package ru.biatech.voip.service;

import ru.biatech.voip.model.Cdr;

import java.sql.Timestamp;
import java.util.List;

public interface AgentNameService {
    void processCdr(Cdr cdr);

    List<Cdr> getCdrListBetweenDates(Timestamp date1, Timestamp date2);

    List<Cdr> getCdrListUntilDate(Timestamp date1);

    boolean isCdrSuitable(Cdr cdr);
}
