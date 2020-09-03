package com.crpdev.msscoilorder.service.listeners;

import com.crpdev.factory.oil.model.events.ValidateOrderResult;
import com.crpdev.msscoilorder.config.JmsConfig;
import com.crpdev.msscoilorder.service.OilOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by rajapandian
 * Date: 03/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.msscoilorder.service.listeners
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationResultListener {

    private final OilOrderManager oilOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_RESPONSE_QUEUE)
    public void listen(ValidateOrderResult result){
        final UUID oilOrderId = result.getOrderId();
        log.debug("Validation result for order id: " + oilOrderId);

        oilOrderManager.processValidationResult(result);
    }

}
