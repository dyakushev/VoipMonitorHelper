**Что делает.**

Делаем запрос в таблицу cdr Voipmonitor, в зависимости от номера:
    asterisk - получает реальный номер из базы Asterisk, обновляет его в бд Voipmonitor
    cucm - делаем запросы в таблицы, в след. последовательноти Termination_Call_Detail, Agent_RealTime, Agent_Logout, если для номера есть оператор - записываем информацию об ид, логину, enterprise_name, calltype, skill в таблицу cdr_agent.


**Пишет логи работы в папку /data/cdrmodifier/logs/**

    application.log  - логи по работе приложения в целом
    agent_name.log - логи по агентам
    asterisk_extension.log - логи по номерам астериска


**Конфигурационные файлы**

    application.properties - конфигурация БД, config.StartTastEveryMiliseconds - как часто запускаем задачу запроса в воипмонитор в ms, config.CheckCdrBeforeMinute - запрашивать cdr раньше 2 минут чем сейчас(сделано, чтобы запись в HDS появилась гарантированно)
    log4j2.xml - настройки логирования
    
**Где лежит**

    Исполняемый файл jar лежит на сервере c7-callrec.tlc.lan а папке /data/cdrmodifier/
    Для приложения создан сервис Centos, местоположение /usr/lib/systemd/system/cdrmodifier.service
    
**Управление сервисом**

    Запускается как сервис - systemctl start cdrmodifier
    Проверка статуса - systemctl status cdrmodifier
    Завершение - systemctl stop cdrmodifier
    Настроен автозапуск приложения.
