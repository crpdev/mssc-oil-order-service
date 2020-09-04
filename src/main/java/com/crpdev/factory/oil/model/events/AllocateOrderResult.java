package com.crpdev.factory.oil.model.events;

import com.crpdev.factory.oil.model.OilOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by rajapandian
 * Date: 04/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.factory.oil.model.events
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocateOrderResult {

    private OilOrderDto oilOrderDto;
    private Boolean allocationError = false;
    private Boolean pendingInventory = false;
}
