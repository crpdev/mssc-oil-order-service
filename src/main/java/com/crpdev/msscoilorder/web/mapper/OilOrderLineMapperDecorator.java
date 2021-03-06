package com.crpdev.msscoilorder.web.mapper;

import com.crpdev.factory.oil.model.OilOrderLineDto;
import com.crpdev.factory.oil.model.oil.OilDto;
import com.crpdev.msscoilorder.domain.OilOrderLine;
import com.crpdev.msscoilorder.service.oil.OilService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public abstract class OilOrderLineMapperDecorator implements OilOrderLineMapper {

    private OilService oilService;
    private OilOrderLineMapper oilOrderLineMapper;

    @Autowired
    public void setOilService(OilService oilService) {
        this.oilService = oilService;
    }

    @Autowired
    public void setOilOrderLineMapper(OilOrderLineMapper oilOrderLineMapper) {
        this.oilOrderLineMapper = oilOrderLineMapper;
    }

    @Override
    public OilOrderLineDto toOilOrderLineDto(OilOrderLine oilOrderLine) {
        OilOrderLineDto dto = oilOrderLineMapper.toOilOrderLineDto(oilOrderLine);
        Optional<OilDto> oilDtoOptional = oilService.getOilByProductCode(oilOrderLine.getProductCode());
        oilDtoOptional.ifPresent(oilDto -> {
            dto.setOilName(oilDto.getOilName());
            dto.setOilType(oilDto.getOilType());
            dto.setOilId(oilDto.getId());
            dto.setPrice(oilDto.getPrice());
        });

        return dto;
    }
}
