package com.crpdev.msscoilorder.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OilOrderLineDto extends BaseItem {

    @Builder
    public OilOrderLineDto(UUID id, Integer version, OffsetDateTime createDate, OffsetDateTime lastModifiedDate, String barCode, String oilName, UUID oilId, Integer orderQuantity) {
        super(id, version, createDate, lastModifiedDate);
        this.barCode = barCode;
        this.oilName = oilName;
        this.oilId = oilId;
        this.orderQuantity = orderQuantity;
    }

    private String barCode;
    private String oilName;
    private UUID oilId;
    private Integer orderQuantity = 0;
}
