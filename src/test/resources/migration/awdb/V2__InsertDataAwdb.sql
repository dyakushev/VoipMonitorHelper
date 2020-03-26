insert into t_Agent (EnterpriseName, PersonID, PeripheralNumber, SkillTargetID)
values ('Testoviy_test1', 1, '1', 1);

insert into t_Agent (EnterpriseName, PersonID, PeripheralNumber, SkillTargetID)
values ('Testoviy_test2', 2, '2', 2);

insert into t_Agent (EnterpriseName, PersonID, PeripheralNumber, SkillTargetID)
values ('Testoviy_test3', 3, '3', 3);

insert into t_Agent_Logout (SkillTargetID, Extension, LogoutDateTime)
values (1, '1000', '2020-03-17T14:00:00');


insert into t_Agent_Real_Time(SkillTargetID, SkillGroupSkillTargetID, Extension)
values (2, 2, '1001');

insert into t_Person(LoginName, PersonID)
values ('ttestoviy1', 1);
insert into t_Person(LoginName, PersonID)
values ('ttestoviy2', 2);


insert into t_Skill_Group (EnterpriseName, SkillTargetID)
values ('test_skill_1', 1);

insert into t_Call_Type(EnterpriseName, CallTypeID)
values ('test_call_type_1', 1);

