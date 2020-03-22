CREATE TABLE t_Agent
(
    EnterpriseName   varchar(255),
    PersonID         int,
    PeripheralNumber varchar(255),
    SkillTargetID    int
);
CREATE TABLE t_Agent_Logout
(
    SkillTargetID  int,
    Extension      varchar(255),
    LogoutDateTime DATETIME

);
CREATE TABLE t_Agent_Real_Time
(
    SkillTargetID           int,
    SkillGroupSkillTargetID int,
    Extension               varchar(255)
);
CREATE TABLE t_Person
(
    LoginName varchar(255),
    PersonID  int
);
CREATE TABLE t_Call_Type
(
    EnterpriseName varchar(255),
    CallTypeID     int
);
CREATE TABLE t_Skill_Group
(
    EnterpriseName varchar(255),
    SkillTargetID  int
);

