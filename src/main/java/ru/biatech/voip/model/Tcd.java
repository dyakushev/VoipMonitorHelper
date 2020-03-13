package ru.biatech.voip.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tcd {
    private String agentPeripheralNumber;
    private Integer agentSkillId;
    private Integer callTypeId;

}
