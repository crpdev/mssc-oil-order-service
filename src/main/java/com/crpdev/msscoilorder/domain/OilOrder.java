package com.crpdev.msscoilorder.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OilOrder extends BaseEntity {

    @Builder
    public OilOrder(UUID id, Integer version, Timestamp createDate, Timestamp lastModifiedDate, String customerRef, Customer customer, Set<OilOrderLine> oilOrderLines, OilOrderStatusEnum orderStatus, String orderStatusCallbackUrl) {
        super(id, version, createDate, lastModifiedDate);
        this.customerRef = customerRef;
        this.customer = customer;
        this.oilOrderLines = oilOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
    }

    private String customerRef;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "oilOrder", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private Set<OilOrderLine> oilOrderLines;

    private OilOrderStatusEnum orderStatus = OilOrderStatusEnum.NEW;
    private String orderStatusCallbackUrl;
}
