insert into cdr (ID, calldate, callend, duration, caller, called)
values (1, NOW(), DATEADD(MINUTE, 1, NOW()), 60, '1000000000', '89999999999');

insert into cdr_agent (cdr_id, agent_id, agent_login, agent_enterprise_name, agent_skill, call_type)
values (1, '1', 'testoviy', 'Test_Testoviy', null, null);