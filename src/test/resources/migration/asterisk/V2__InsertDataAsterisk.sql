--test 1, asterisk extension ok, length = 9
insert into extensions (exten, appdata)
values ('50000', 'dialing,s,1,(100000000)');
--test 2,two extensions with the same appdata
insert into extensions (exten, appdata)
values ('50001', 'dialing,s,1,(100000002)');
insert into extensions (exten, appdata)
values ('50002', 'dialing,s,1,(100000002)');