package com.crpdev.factory.oil.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OilOrderLineDto extends BaseItem {

    @Builder
    public OilOrderLineDto(UUID id, Integer version, OffsetDateTime createDate, OffsetDateTime lastModifiedDate, String productCode, String oilName, String oilType, UUID oilId, Integer orderQuantity, BigDecimal price) {
        super(id, version, createDate, lastModifiedDate);
        this.productCode = productCode;
        this.oilName = oilName;
        this.oilType = oilType;
        this.oilId = oilId;
        this.orderQuantity = orderQuantity;
        this.price = price;
    }

    private String productCode;
    private String oilName;
    private String oilType;
    private UUID oilId;
    private Integer orderQuantity = 0;
    private BigDecimal price;
    private Integer quantityAllocated = 0;
}
