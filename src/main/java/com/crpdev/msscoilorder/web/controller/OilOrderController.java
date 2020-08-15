package com.crpdev.msscoilorder.web.controller;

import com.crpdev.msscoilorder.service.OilOrderService;
import com.crpdev.msscoilorder.web.model.OilOrderDto;
import com.crpdev.msscoilorder.web.model.OilOrderPagedList;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/customers/{customerId}/")
@RestController
public class OilOrderController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final OilOrderService oilOrderService;

    public OilOrderController(OilOrderService oilOrderService) {
        this.oilOrderService = oilOrderService;
    }

    @GetMapping("orders")
    public OilOrderPagedList listOrders(@PathVariable("customerId") UUID customerId,
                                        @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                        @RequestParam(value = "pageSize", required = false) Integer pageSize){

        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return oilOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OilOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody OilOrderDto oilOrderDto){
        return oilOrderService.placeOrder(customerId, oilOrderDto);
    }

    @GetMapping("orders/{orderId}")
    public OilOrderDto getOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId){
        return oilOrderService.getOrderById(customerId, orderId);
    }

    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId){
        oilOrderService.pickupOrder(customerId, orderId);
    }
}
