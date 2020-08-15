package com.crpdev.msscoilorder.service;

import com.crpdev.msscoilorder.web.model.OilOrderDto;
import com.crpdev.msscoilorder.web.model.OilOrderPagedList;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OilOrderService {

    OilOrderPagedList listOrders(UUID customerId, Pageable pageable);

    OilOrderDto placeOrder(UUID customerId, OilOrderDto oilOrderDto);

    OilOrderDto getOrderById(UUID customerId, UUID orderId);

    void pickupOrder(UUID customerId, UUID orderId);

}
