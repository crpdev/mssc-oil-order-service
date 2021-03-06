package com.crpdev.msscoilorder.service;

import com.crpdev.factory.oil.model.OilOrderDto;
import com.crpdev.factory.oil.model.OilOrderLineDto;
import com.crpdev.msscoilorder.bootstrap.OilOrderBootstrap;
import com.crpdev.msscoilorder.domain.Customer;
import com.crpdev.msscoilorder.repository.CustomerRepository;
import com.crpdev.msscoilorder.repository.OilOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class WholeSaleService {

    private final CustomerRepository customerRepository;
    private final OilOrderService oilOrderService;
    private final OilOrderRepository oilOrderRepository;
    private final List<String> oilProductCodes = new ArrayList<>(3);

    public WholeSaleService(CustomerRepository customerRepository, OilOrderService oilOrderService, OilOrderRepository oilOrderRepository) {
        this.customerRepository = customerRepository;
        this.oilOrderService = oilOrderService;
        this.oilOrderRepository = oilOrderRepository;

        oilProductCodes.add(OilOrderBootstrap.OIL_PRODUCT_CODE_1);
        oilProductCodes.add(OilOrderBootstrap.OIL_PRODUCT_CODE_2);
        oilProductCodes.add(OilOrderBootstrap.OIL_PRODUCT_CODE_3);
    }

    @Transactional
    @Scheduled(fixedRate = 2000) //run every 2 seconds
    public void placeWholeSaleOrder(){
        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(OilOrderBootstrap.DEMO_ROOM);

        if (customerList.size() == 1){ //should be just one
            doPlaceOrder(customerList.get(0));
        } else {
            log.error("Too many or too few demo room customers found");
        }
    }

    private void doPlaceOrder(Customer customer) {
        String oilToOrder = getRandomOilProductCode();

        OilOrderLineDto oilOrderLine = OilOrderLineDto.builder()
                .productCode(oilToOrder)
                .orderQuantity(new Random().nextInt(6)) //todo externalize value to property
                .build();

        List<OilOrderLineDto> oilOrderLineSet = new ArrayList<>();
        oilOrderLineSet.add(oilOrderLine);

        OilOrderDto beerOrder = OilOrderDto.builder()
                .customerId(customer.getId())
                .customerRef(UUID.randomUUID().toString())
                .oilOrderLines(oilOrderLineSet)
                .build();

        OilOrderDto savedOrder = oilOrderService.placeOrder(customer.getId(), beerOrder);

    }

    private String getRandomOilProductCode() {
        return oilProductCodes.get(new Random().nextInt(oilProductCodes.size() -0));
    }
}
