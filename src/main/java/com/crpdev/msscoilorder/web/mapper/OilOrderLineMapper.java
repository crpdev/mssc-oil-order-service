package com.crpdev.msscoilorder.web.mapper;

import com.crpdev.msscoilorder.domain.OilOrderLine;
import com.crpdev.msscoilorder.web.model.OilOrderLineDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface OilOrderLineMapper {

    OilOrderLineDto toOilOrderLine(OilOrderLine oilOrderLine);

    OilOrderLine toOilOrder(OilOrderLineDto oilOrderLineDto);
}
