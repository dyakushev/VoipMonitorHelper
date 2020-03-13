package ru.biatech.voip.repo;

import ru.biatech.voip.model.Tcd;

import java.sql.Timestamp;
import java.util.Optional;

public interface HdsJdbcRepo {
    Optional<Tcd> getTcdByDateAndExtentionAndAni(Timestamp callDate, Timestamp callEnd, Integer duration, Long extension, String ani);

    Optional<Tcd> getTcdByDateAndExtention(Timestamp callDate, Timestamp callEnd, Integer duration, Long extension);
}
