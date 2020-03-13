package ru.biatech.voip.repo;

import ru.biatech.voip.model.Extension;

import java.util.Optional;

public interface AsteriskJdbcRepo {
    Optional<Extension> getExtenByAppdata(String appdata);

}
