package com.crpdev.msscoilorder.service.listeners;

import com.crpdev.factory.oil.model.events.AllocateOrderResult;
import com.crpdev.msscoilorder.config.JmsConfig;
import com.crpdev.msscoilorder.service.OilOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Created by rajapandian
 * Date: 04/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.msscoilorder.service.listeners
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class OrderAllocationResultListener {

    private final OilOrderManager oilOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResult result){
        if (!result.getAllocationError() && !result.getPendingInventory()){
            log.debug("Allocation Success");
            oilOrderManager.oilOrderAllocationPassed(result.getOilOrderDto());
        } else if (!result.getAllocationError() && result.getPendingInventory()){
            log.debug("Pending Inventory");
            oilOrderManager.oilOrderAllocationPendingInventory(result.getOilOrderDto());
        } else if (result.getAllocationError()){
            log.debug("Allocation Exception");
            oilOrderManager.oilOrderAllocationFailed(result.getOilOrderDto());
        }
    }

}
