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
public class OrderStatusUpdate extends BaseItem {

    @Builder
    public OrderStatusUpdate(UUID id, Integer version, OffsetDateTime createDate, OffsetDateTime lastModifiedDate, UUID id1, String customerRef, String orderStatus) {
        super(id, version, createDate, lastModifiedDate);
        this.id = id1;
        this.customerRef = customerRef;
        this.orderStatus = orderStatus;
    }

    private UUID id;
    private String customerRef;
    private String orderStatus;
}
