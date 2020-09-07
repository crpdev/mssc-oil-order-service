package com.crpdev.msscoilorder.service;

import com.crpdev.factory.oil.model.OilOrderDto;
import com.crpdev.factory.oil.model.OilOrderPagedList;
import com.crpdev.msscoilorder.domain.Customer;
import com.crpdev.msscoilorder.domain.OilOrder;
import com.crpdev.msscoilorder.domain.OilOrderStatusEnum;
import com.crpdev.msscoilorder.repository.CustomerRepository;
import com.crpdev.msscoilorder.repository.OilOrderRepository;
import com.crpdev.msscoilorder.web.mapper.OilOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OilOrderServiceImpl implements OilOrderService {

    private final OilOrderRepository oilOrderRepository;
    private final CustomerRepository customerRepository;
    private final OilOrderMapper oilOrderMapper;
    private final OilOrderManager oilOrderManager;


    @Override
    public OilOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if(customerOptional.isPresent()){
            Page<OilOrder> oilOrderPage = oilOrderRepository.findAllByCustomer(customerOptional.get(), pageable);
            return new OilOrderPagedList(oilOrderPage.stream()
            .map(oilOrderMapper::toOilOrderDto)
            .collect(Collectors.toList()),PageRequest.of(oilOrderPage.getPageable().getPageNumber(),
                    oilOrderPage.getPageable().getPageSize()), oilOrderPage.getTotalElements());
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public OilOrderDto placeOrder(UUID customerId, OilOrderDto oilOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if(customerOptional.isPresent()){
            OilOrder oilOrder = oilOrderMapper.toOilOrder(oilOrderDto);
            oilOrder.setId(null);
            oilOrder.setCustomer(customerOptional.get());
            oilOrder.setOrderStatus(OilOrderStatusEnum.NEW);

            oilOrder.getOilOrderLines().forEach(line -> line.setOilOrder(oilOrder));
            OilOrder savedOilOrder = oilOrderManager.newOilOrder(oilOrder);

            log.debug("Saved Oil Order: " + savedOilOrder.getId());
            return oilOrderMapper.toOilOrderDto(savedOilOrder);
        } else {
            return null;
        }
    }

    @Override
    public OilOrderDto getOrderById(UUID customerId, UUID orderId) {
        return oilOrderMapper.toOilOrderDto(getOrder(customerId, orderId));
    }

    @Transactional
    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        oilOrderManager.pickupOrder(orderId);
    }

    private OilOrder getOrder(UUID customerId, UUID orderId){
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if(customerOptional.isPresent()){
            Optional<OilOrder> beerOrderOptional = oilOrderRepository.findById(orderId);

            if(beerOrderOptional.isPresent()){
                OilOrder oilOrder = beerOrderOptional.get();

                // fall to exception if customer id's do not match - order not for customer
                if(oilOrder.getCustomer().getId().equals(customerId)){
                    return oilOrder;
                }
            }
            throw new RuntimeException("Oil Order Not Found");
        }
        throw new RuntimeException("Customer Not Found");
    }
}
