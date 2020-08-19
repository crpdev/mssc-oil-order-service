package com.crpdev.msscoilorder.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OilOrderLine extends BaseEntity {

    @Builder
    public OilOrderLine(UUID id, Integer version, Timestamp createDate, Timestamp lastModifiedDate, OilOrder oilOrder, UUID oilId, String barCode, Integer orderQuantity, Integer quantityAllocated) {
        super(id, version, createDate, lastModifiedDate);
        this.oilOrder = oilOrder;
        this.oilId = oilId;
        this.barCode = barCode;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }

    @ManyToOne
    private OilOrder oilOrder;

    private UUID oilId;
    private String barCode;
    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;
}
