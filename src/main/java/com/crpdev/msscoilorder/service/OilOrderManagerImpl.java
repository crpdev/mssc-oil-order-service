package com.crpdev.msscoilorder.service;

import com.crpdev.factory.oil.model.OilOrderDto;
import com.crpdev.factory.oil.model.events.ValidateOrderResult;
import com.crpdev.msscoilorder.domain.OilOrder;
import com.crpdev.msscoilorder.domain.OilOrderEventEnum;
import com.crpdev.msscoilorder.domain.OilOrderStatusEnum;
import com.crpdev.msscoilorder.repository.OilOrderRepository;
import com.crpdev.msscoilorder.sm.OilOrderStateChangeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

/**
 * Created by rajapandian
 * Date: 01/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.msscoilorder.service
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class OilOrderManagerImpl implements OilOrderManager {

    public static final String ORDER_ID_HEADER = "order_id";

    private final StateMachineFactory<OilOrderStatusEnum, OilOrderEventEnum> smFactory;
    private final OilOrderRepository oilOrderRepository;
    private final OilOrderStateChangeInterceptor oilOrderStateChangeInterceptor;

    @Override
    public OilOrder newOilOrder(OilOrder oilOrder) {
        oilOrder.setId(null);
        oilOrder.setOrderStatus(OilOrderStatusEnum.NEW.toString());
        OilOrder savedOilOrder = oilOrderRepository.save(oilOrder);
        sendOilOrderEvent(savedOilOrder, OilOrderEventEnum.VALIDATE_ORDER);
        return savedOilOrder;
    }

    @Override
    public void processValidationResult(ValidateOrderResult result) {
        OilOrder oilOrder = oilOrderRepository.getOne(result.getOrderId());

        if (result.getIsValid()){
            sendOilOrderEvent(oilOrder, OilOrderEventEnum.VALIDATION_PASSED);
            OilOrder validatedOrder = oilOrderRepository.findOneById(oilOrder.getId());
            sendOilOrderEvent(validatedOrder, OilOrderEventEnum.ALLOCATE_ORDER);

        } else {
            sendOilOrderEvent(oilOrder, OilOrderEventEnum.VALIDATION_FAILED);
        }
    }

    @Override
    public void oilOrderAllocationPassed(OilOrderDto oilOrderDto) {
        OilOrder oilOrder = oilOrderRepository.getOne(oilOrderDto.getId());
        sendOilOrderEvent(oilOrder, OilOrderEventEnum.ALLOCATION_SUCCESS);
        updateAllocatedQty(oilOrderDto);
    }

    private void updateAllocatedQty(OilOrderDto oilOrderDto) {
        OilOrder allocatedOrder = oilOrderRepository.getOne(oilOrderDto.getId());
        allocatedOrder.getOilOrderLines().forEach(oilOrderLine -> {
           oilOrderDto.getOilOrderLines().forEach(oilOrderLineDto -> {
               if (oilOrderLine.getId().equals(oilOrderLineDto.getId())){
                   oilOrderLine.setQuantityAllocated(oilOrderLineDto.getQuantityAllocated());
               }
           });
        });
        oilOrderRepository.saveAndFlush(allocatedOrder);
    }

    @Override
    public void oilOrderAllocationPendingInventory(OilOrderDto oilOrderDto) {
        OilOrder oilOrder = oilOrderRepository.getOne(oilOrderDto.getId());
        sendOilOrderEvent(oilOrder, OilOrderEventEnum.ALLOCATION_NO_INVENTORY);
    }

    @Override
    public void oilOrderAllocationFailed(OilOrderDto oilOrderDto) {
        OilOrder oilOrder = oilOrderRepository.getOne(oilOrderDto.getId());
        sendOilOrderEvent(oilOrder, OilOrderEventEnum.ALLOCATION_FAILED);
    }

    private void sendOilOrderEvent(OilOrder oilOrder, OilOrderEventEnum eventEnum) {
        StateMachine<OilOrderStatusEnum, OilOrderEventEnum> sm = build(oilOrder);
        Message msg = MessageBuilder.withPayload(eventEnum)
                .setHeader(ORDER_ID_HEADER, oilOrder.getId().toString())
                .build();
        sm.sendEvent(msg);
    }

    private StateMachine<OilOrderStatusEnum, OilOrderEventEnum> build(OilOrder oilOrder) {
        StateMachine<OilOrderStatusEnum, OilOrderEventEnum> sm = smFactory.getStateMachine(oilOrder.getId());
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(oilOrderStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext(oilOrder.getOrderStatus(), null, null, null));
                });
        return sm;
    }
}
