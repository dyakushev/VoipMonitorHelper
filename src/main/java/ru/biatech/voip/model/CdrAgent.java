package ru.biatech.voip.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CdrAgent {
    private Long cdrId;
    private String agentId;
    private String agentLogin;
    private String agentEnterpriseName;
    private String agentSkill;
    private String callType;
}
