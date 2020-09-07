package com.crpdev.msscoilorder.services.testcomponents;

import com.crpdev.factory.oil.model.events.ValidateOrderRequest;
import com.crpdev.factory.oil.model.events.ValidateOrderResult;
import com.crpdev.msscoilorder.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * Created by rajapandian
 * Date: 07/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.msscoilorder.services.testcomponents
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class OilOrderValidationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void listen(Message msg){
        ValidateOrderRequest request = (ValidateOrderRequest) msg.getPayload();
        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_RESPONSE_QUEUE, ValidateOrderResult.builder()
            .isValid(true)
            .orderId(request.getOilOrderDto().getId())
            .build());
    }

}
