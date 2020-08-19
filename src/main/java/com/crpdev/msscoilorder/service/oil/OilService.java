package com.crpdev.msscoilorder.service.oil;

import com.crpdev.msscoilorder.web.model.oil.OilDto;

import java.util.Optional;
import java.util.UUID;

public interface OilService {

    Optional<OilDto> getOilById(UUID oilId);
    Optional<OilDto> getOilByBarCode(String barCode);
}
