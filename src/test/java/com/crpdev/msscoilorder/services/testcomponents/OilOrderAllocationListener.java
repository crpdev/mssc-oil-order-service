package com.crpdev.msscoilorder.services.testcomponents;

import com.crpdev.factory.oil.model.events.AllocateOrderRequest;
import com.crpdev.factory.oil.model.events.AllocateOrderResult;
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
public class OilOrderAllocationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(Message msg){

        AllocateOrderRequest request = (AllocateOrderRequest) msg.getPayload();
        log.debug("Allocation event message received for Order Id: " + request.getOilOrderDto().getId());

        request.getOilOrderDto().getOilOrderLines().forEach(oilOrderLineDto -> {
            log.debug("Processing Order Lines: " + oilOrderLineDto.getOrderQuantity());
            oilOrderLineDto.setQuantityAllocated(oilOrderLineDto.getOrderQuantity());
        });

        log.debug("Sending Allocation Response");
        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, AllocateOrderResult.builder()
                .oilOrderDto(request.getOilOrderDto())
                .allocationError(false)
                .pendingInventory(false)
                .build());

    }

}
