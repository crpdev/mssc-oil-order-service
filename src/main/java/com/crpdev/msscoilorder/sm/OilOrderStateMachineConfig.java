package com.crpdev.msscoilorder.sm;

import com.crpdev.msscoilorder.domain.OilOrderEventEnum;
import com.crpdev.msscoilorder.domain.OilOrderStatusEnum;
import com.crpdev.msscoilorder.sm.actions.AllocateOrderAction;
import com.crpdev.msscoilorder.sm.actions.ValidateOrderAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

/**
 * Created by rajapandian
 * Date: 01/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.msscoilorder.sm
 **/
@Slf4j
@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class OilOrderStateMachineConfig extends StateMachineConfigurerAdapter<OilOrderStatusEnum, OilOrderEventEnum> {

    private final ValidateOrderAction validateOrderAction;
    private final AllocateOrderAction allocateOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<OilOrderStatusEnum, OilOrderEventEnum> states) throws Exception {
        log.debug("<<< Configuring State Machine States >>>");
        states.withStates().initial(OilOrderStatusEnum.NEW)
                .states(EnumSet.allOf(OilOrderStatusEnum.class))
                .end(OilOrderStatusEnum.PICKED_UP)
                .end(OilOrderStatusEnum.DELIVERED)
                .end(OilOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(OilOrderStatusEnum.ALLOCATION_EXCEPTION)
                .end(OilOrderStatusEnum.VALIDATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OilOrderStatusEnum, OilOrderEventEnum> transitions) throws Exception {
        log.debug("<<< Configuring State Machine Transitions >>>");
        transitions.withExternal()
                    .source(OilOrderStatusEnum.NEW).target(OilOrderStatusEnum.VALIDATION_PENDING)
                    .event(OilOrderEventEnum.VALIDATE_ORDER)
                    .action(validateOrderAction)
                .and().withExternal()
                    .source(OilOrderStatusEnum.VALIDATION_PENDING).target(OilOrderStatusEnum.VALIDATED)
                    .event(OilOrderEventEnum.VALIDATION_PASSED)
                .and().withExternal()
                    .source(OilOrderStatusEnum.VALIDATION_PENDING).target(OilOrderStatusEnum.VALIDATION_EXCEPTION)
                    .event(OilOrderEventEnum.VALIDATION_FAILED)
                .and().withExternal()
                    .source(OilOrderStatusEnum.VALIDATED).target(OilOrderStatusEnum.ALLOCATION_PENDING)
                    .event(OilOrderEventEnum.ALLOCATE_ORDER)
                    .action(allocateOrderAction)
                .and().withExternal()
                    .source(OilOrderStatusEnum.ALLOCATION_PENDING).target(OilOrderStatusEnum.ALLOCATED)
                    .event(OilOrderEventEnum.ALLOCATION_SUCCESS)
                .and().withExternal()
                    .source(OilOrderStatusEnum.ALLOCATION_PENDING).target(OilOrderStatusEnum.ALLOCATION_EXCEPTION)
                    .event(OilOrderEventEnum.ALLOCATION_FAILED)
                .and().withExternal()
                    .source(OilOrderStatusEnum.ALLOCATION_PENDING).target(OilOrderStatusEnum.PENDING_INVENTORY)
                    .event(OilOrderEventEnum.ALLOCATION_NO_INVENTORY)
                .and().withExternal()
                    .source(OilOrderStatusEnum.ALLOCATED).target(OilOrderStatusEnum.PICKED_UP)
                    .event(OilOrderEventEnum.ORDER_PICKED_UP)
                ;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OilOrderStatusEnum, OilOrderEventEnum> config) throws Exception {
        StateMachineListenerAdapter<OilOrderStatusEnum, OilOrderEventEnum> adapter = new StateMachineListenerAdapter(){
            @Override
            public void stateChanged(State from, State to) {
                log.info(String.format("State Changed from: %s > to: %s", from, to));
            }
        };
        config.withConfiguration().listener(adapter);
    }
}
