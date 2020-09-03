package com.crpdev.msscoilorder.service;

import com.crpdev.factory.oil.model.events.ValidateOrderResult;
import com.crpdev.msscoilorder.domain.OilOrder;

/**
 * Created by rajapandian
 * Date: 01/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.msscoilorder.service
 **/
public interface OilOrderManager {
    OilOrder newOilOrder(OilOrder oilOrder);

    void processValidationResult(ValidateOrderResult result);
}