CREATE TABLE cdr
(
    ID       bigint,
    calldate datetime,
    callend  datetime,
    duration int,
    caller   varchar(255),
    called   varchar(255)
);
CREATE TABLE cdr_agent
(
    cdr_id                bigint,
    agent_id              varchar(255),
    agent_login           varchar(255),
    agent_enterprise_name varchar(255),
    agent_skill           varchar(255),
    call_type             varchar(255)
);