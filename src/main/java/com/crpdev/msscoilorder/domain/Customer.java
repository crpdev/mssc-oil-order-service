package com.crpdev.msscoilorder.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Customer extends BaseEntity {

    @Builder
    public Customer(UUID id, Integer version, Timestamp createDate, Timestamp lastModifiedDate, String customerName, UUID apiKey, Set<OilOrder> oilOrders) {
        super(id, version, createDate, lastModifiedDate);
        this.customerName = customerName;
        this.apiKey = apiKey;
        this.oilOrders = oilOrders;
    }

    private String customerName;

    @Column(length = 36, columnDefinition = "varchar")
    private UUID apiKey;

    @OneToMany(mappedBy = "customer")
    private Set<OilOrder> oilOrders;

}
