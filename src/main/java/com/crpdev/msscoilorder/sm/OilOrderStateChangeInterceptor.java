package com.crpdev.msscoilorder.sm;

import com.crpdev.msscoilorder.domain.OilOrder;
import com.crpdev.msscoilorder.domain.OilOrderEventEnum;
import com.crpdev.msscoilorder.domain.OilOrderStatusEnum;
import com.crpdev.msscoilorder.repository.OilOrderRepository;
import com.crpdev.msscoilorder.service.OilOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by rajapandian
 * Date: 01/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.msscoilorder.sm
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class OilOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<OilOrderStatusEnum, OilOrderEventEnum> {

    private final OilOrderRepository oilOrderRepository;

    @Transactional
    @Override
    public void preStateChange(State<OilOrderStatusEnum, OilOrderEventEnum> state, Message<OilOrderEventEnum> message, Transition<OilOrderStatusEnum, OilOrderEventEnum> transition, StateMachine<OilOrderStatusEnum, OilOrderEventEnum> stateMachine) {
        log.debug("<<< Received Interceptor Action >>> " + state.getId());
        Optional.ofNullable(message).ifPresent(msg -> {
                Optional.ofNullable((String)(msg.getHeaders().getOrDefault(OilOrderManagerImpl.ORDER_ID_HEADER, -1)))
                        .ifPresent(orderId -> {
                            log.debug("<<< Change of State >>> " + state.getId() + " For Order Id: " + orderId);
                            OilOrder oilOrder = oilOrderRepository.getOne(UUID.fromString(orderId));
                            oilOrder.setOrderStatus(state.getId());
                            oilOrderRepository.saveAndFlush(oilOrder);//Hibernate does a lazy write by default. Forcing immediate write using saveAndFlush
                        });
            });
        }
    }
