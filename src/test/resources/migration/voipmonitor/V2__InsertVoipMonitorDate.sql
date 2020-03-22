insert into cdr (ID, calldate, callend, duration, caller, called)
values (1, NOW(), DATEADD(MINUTE,1,NOW()),60, '1000000000', '89999999999');