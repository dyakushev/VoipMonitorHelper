package ru.biatech.voip.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CdrNext {
    private Long cdrId;
    private String agentName;
    private String callTypeName;
    private String skillGroupName;
    private String agentLogin;
    private String agentId;
}
