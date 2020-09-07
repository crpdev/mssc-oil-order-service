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
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

    @Transactional
    @Override
    public OilOrder newOilOrder(OilOrder oilOrder) {
        oilOrder.setId(null);
        oilOrder.setOrderStatus(OilOrderStatusEnum.NEW);
        OilOrder savedOilOrder = oilOrderRepository.save(oilOrder);
        log.debug("Saved Oil Order: " + savedOilOrder.getId());
        sendOilOrderEvent(savedOilOrder, OilOrderEventEnum.VALIDATE_ORDER);
        log.debug("Saved Oil Order State: " + savedOilOrder.getOrderStatus());
        return savedOilOrder;
    }

    @Transactional
    @Override
    public void processValidationResult(ValidateOrderResult result) {
        log.debug("<<< Processing Validation Result For Order Id: " + result.getOrderId());
        OilOrder oilOrder = oilOrderRepository.getOne(result.getOrderId());

        if (result.getIsValid()){
            log.debug("<<< Sending Validation Passed Event For Order Id: " + result.getOrderId());
            sendOilOrderEvent(oilOrder, OilOrderEventEnum.VALIDATION_PASSED);
            OilOrder validatedOrder = oilOrderRepository.findById(oilOrder.getId()).get();
            log.debug("<<< Validated Order Id: " + result.getOrderId() + ": Status: " + validatedOrder.getOrderStatus());
            sendOilOrderEvent(validatedOrder, OilOrderEventEnum.ALLOCATE_ORDER);
            log.debug("<<< Sending Allocate Order Event For Order Id: " + result.getOrderId());
        } else {
            sendOilOrderEvent(oilOrder, OilOrderEventEnum.VALIDATION_FAILED);
        }
    }

    @Transactional
    @Override
    public void oilOrderAllocationPassed(OilOrderDto oilOrderDto) {
        OilOrder oilOrder = oilOrderRepository.findById(oilOrderDto.getId()).get();
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

    @Transactional
    @Override
    public void pickupOrder(UUID oilId) {
        log.debug("<<< Processing Order Pickup For Order Id: " + oilId);
        OilOrder oilOrder = oilOrderRepository.findById(oilId).get();
        sendOilOrderEvent(oilOrder, OilOrderEventEnum.ORDER_PICKED_UP);
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
        log.debug("Sent Oil Order Event For OrderId: " + oilOrder.getId() + " with Event: " + eventEnum);
        sm.sendEvent(msg);
    }

    private StateMachine<OilOrderStatusEnum, OilOrderEventEnum> build(OilOrder oilOrder) {
        log.debug("Building State Machine For OrderId: " + oilOrder.getId());
        StateMachine<OilOrderStatusEnum, OilOrderEventEnum> sm = smFactory.getStateMachine(oilOrder.getId());
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(oilOrderStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext(oilOrder.getOrderStatus(), null, null, null));
                });
        sm.start();
        return sm;
    }
}
