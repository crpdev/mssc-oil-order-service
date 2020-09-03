package com.crpdev.msscoilorder.sm.actions;

import com.crpdev.factory.oil.model.events.AllocateOrderRequest;
import com.crpdev.msscoilorder.config.JmsConfig;
import com.crpdev.msscoilorder.domain.OilOrder;
import com.crpdev.msscoilorder.domain.OilOrderEventEnum;
import com.crpdev.msscoilorder.domain.OilOrderStatusEnum;
import com.crpdev.msscoilorder.repository.OilOrderRepository;
import com.crpdev.msscoilorder.service.OilOrderManagerImpl;
import com.crpdev.msscoilorder.web.mapper.OilOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by rajapandian
 * Date: 03/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.msscoilorder.sm.actions
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class AllocateOrderAction implements Action<OilOrderStatusEnum, OilOrderEventEnum> {

    private final JmsTemplate jmsTemplate;
    private final OilOrderRepository oilOrderRepository;
    private final OilOrderMapper oilOrderMapper;

    @Override
    public void execute(StateContext<OilOrderStatusEnum, OilOrderEventEnum> stateContext) {
        final String oilOrderId = (String)stateContext.getMessageHeader(OilOrderManagerImpl.ORDER_ID_HEADER);
        final OilOrder oilOrder = oilOrderRepository.findOneById(UUID.fromString(oilOrderId));

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE, AllocateOrderRequest.builder()
                    .oilOrderDto(oilOrderMapper.toOilOrderDto(oilOrder)).build());

        log.debug("Allocation request sent for order id: " + oilOrderId);
    }
}
