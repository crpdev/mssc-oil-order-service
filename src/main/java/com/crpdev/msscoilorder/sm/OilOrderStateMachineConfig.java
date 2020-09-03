package com.crpdev.msscoilorder.sm;

import com.crpdev.msscoilorder.domain.OilOrderEventEnum;
import com.crpdev.msscoilorder.domain.OilOrderStatusEnum;
import com.crpdev.msscoilorder.sm.actions.AllocateOrderAction;
import com.crpdev.msscoilorder.sm.actions.ValidateOrderAction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

/**
 * Created by rajapandian
 * Date: 01/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.msscoilorder.sm
 **/
@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class OilOrderStateMachineConfig extends StateMachineConfigurerAdapter<OilOrderStatusEnum, OilOrderEventEnum> {

    private final ValidateOrderAction validateOrderAction;
    private final AllocateOrderAction allocateOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<OilOrderStatusEnum, OilOrderEventEnum> states) throws Exception {
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
        transitions.withExternal()
                    .source(OilOrderStatusEnum.NEW).target(OilOrderStatusEnum.NEW)
                    .event(OilOrderEventEnum.VALIDATE_ORDER)
                    .action(validateOrderAction)
                .and().withExternal()
                    .source(OilOrderStatusEnum.NEW).target(OilOrderStatusEnum.VALIDATED)
                    .event(OilOrderEventEnum.VALIDATION_PASSED)
                .and().withExternal()
                    .source(OilOrderStatusEnum.NEW).target(OilOrderStatusEnum.VALIDATION_EXCEPTION)
                    .event(OilOrderEventEnum.VALIDATION_FAILED)
                .and().withExternal()
                    .source(OilOrderStatusEnum.VALIDATED).target(OilOrderStatusEnum.ALLOCATION_PENDING)
                    .event(OilOrderEventEnum.ALLOCATE_ORDER)
                    .action(allocateOrderAction)
                ;
    }
}
