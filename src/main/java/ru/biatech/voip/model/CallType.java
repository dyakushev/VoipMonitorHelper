package ru.biatech.voip.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CallType {
    private String enterpriseName;
}
