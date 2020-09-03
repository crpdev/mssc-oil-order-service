package com.crpdev.msscoilorder.domain;

/**
 * Created by rajapandian
 * Date: 01/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.msscoilorder.domain
 **/
public enum OilOrderStatusEnum {
    NEW, VALIDATED, VALIDATION_PENDING, VALIDATION_EXCEPTION, ALLOCATION_PENDING,
    ALLOCATED, ALLOCATION_EXCEPTION,
    PENDING_INVENTORY, PICKED_UP, DELIVERED, DELIVERY_EXCEPTION
}
