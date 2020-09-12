package com.crpdev.msscoilorder.service;

import com.crpdev.factory.oil.model.OilOrderDto;
import com.crpdev.factory.oil.model.events.ValidateOrderResult;
import com.crpdev.msscoilorder.domain.OilOrder;

import java.util.UUID;

/**
 * Created by rajapandian
 * Date: 01/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.msscoilorder.service
 **/
public interface OilOrderManager {
    OilOrder newOilOrder(OilOrder oilOrder);

    void processValidationResult(ValidateOrderResult result);

    void oilOrderAllocationPassed(OilOrderDto oilOrderDto);

    void oilOrderAllocationPendingInventory(OilOrderDto oilOrderDto);

    void oilOrderAllocationFailed(OilOrderDto oilOrderDto);

    void pickupOrder(UUID orderId);

    void cancelOrder(UUID orderId);
}
