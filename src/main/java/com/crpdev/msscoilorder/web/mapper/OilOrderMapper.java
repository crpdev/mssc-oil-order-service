package com.crpdev.msscoilorder.web.mapper;

import com.crpdev.factory.oil.model.OilOrderDto;
import com.crpdev.msscoilorder.domain.OilOrder;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class, OilOrderLineMapper.class})
public interface OilOrderMapper {
    OilOrderDto toOilOrderDto(OilOrder oilOrder);
    OilOrder toOilOrder(OilOrderDto oilOrderDto);
}
