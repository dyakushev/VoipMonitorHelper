CREATE TABLE t_Termination_Call_Detail
(
    AgentPeripheralNumber   varchar(255),
    SkillGroupSkillTargetID int,
    CallTypeID              int,
    DateTime                DATETIME,
    InstrumentPortNumber    bigint,
    ANI                     varchar(255),
    TalkTime                int
);