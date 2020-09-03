package com.crpdev.msscoilorder.domain;

/**
 * Created by rajapandian
 * Date: 01/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.msscoilorder.domain
 **/
public enum OilOrderEventEnum {
    VALIDATE_ORDER, VALIDATION_PASSED, VALIDATION_FAILED, ALLOCATE_ORDER, ALLOCATION_SUCCESS, ALLOCATION_NO_INVENTORY,
    ALLOCATION_FAILED, ORDER_PICKED_UP;

}
