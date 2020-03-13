package ru.biatech.voip.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Agent {
    private String enterpriseName;
    private Integer personId;
    private String peripheralNumber;
}
