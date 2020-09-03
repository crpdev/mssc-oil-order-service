package com.crpdev.msscoilorder.web.mapper;

import com.crpdev.factory.oil.model.OilOrderLineDto;
import com.crpdev.msscoilorder.domain.OilOrderLine;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(OilOrderLineMapperDecorator.class)
public interface OilOrderLineMapper {

    OilOrderLineDto toOilOrderLineDto(OilOrderLine oilOrderLine);

    OilOrderLine toOilOrderLine(OilOrderLineDto oilOrderLineDto);
}
