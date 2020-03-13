package ru.biatech.voip.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Extension {
    private String exten;
}
