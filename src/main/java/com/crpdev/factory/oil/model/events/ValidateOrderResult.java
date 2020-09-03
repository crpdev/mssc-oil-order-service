package com.crpdev.factory.oil.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Created by rajapandian
 * Date: 02/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.factory.oil.model.events
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateOrderResult {

    private UUID orderId;
    private Boolean isValid;

}
