package com.crpdev.msscoilorder.service.oil;

import com.crpdev.factory.oil.model.oil.OilDto;

import java.util.Optional;
import java.util.UUID;

public interface OilService {

    Optional<OilDto> getOilById(UUID oilId);
    Optional<OilDto> getOilByProductCode(String barCode);
}
