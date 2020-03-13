package ru.biatech.voip.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Cdr {
    private Long Id;
    private Timestamp callDate;
    private Timestamp callEnd;
    private Integer duration;
    private String caller;
    private String called;
}
