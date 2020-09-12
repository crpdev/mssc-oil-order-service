package com.crpdev.msscoilorder.services;

import com.crpdev.factory.oil.model.oil.OilDto;
import com.crpdev.msscoilorder.domain.Customer;
import com.crpdev.msscoilorder.domain.OilOrder;
import com.crpdev.msscoilorder.domain.OilOrderLine;
import com.crpdev.msscoilorder.domain.OilOrderStatusEnum;
import com.crpdev.msscoilorder.repository.CustomerRepository;
import com.crpdev.msscoilorder.repository.OilOrderRepository;
import com.crpdev.msscoilorder.service.OilOrderManager;
import com.crpdev.msscoilorder.service.oil.OilServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by rajapandian
 * Date: 05/09/20
 * Project: mssc-oil-inventory-service
 * Package: com.crpdev.msscoilorder.services
 **/
@Slf4j
@ExtendWith(WireMockExtension.class)
@SpringBootTest
public class OilOrderManagerImplIT {

    @Autowired
    OilOrderManager oilOrderManager;

    @Autowired
    OilOrderRepository oilOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    ObjectMapper objectMapper;

    Customer customer;

    UUID oilId = UUID.randomUUID();

    @TestConfiguration
    static class RestTemplateBuilderProvider {

        @Bean(destroyMethod = "stop")
        public WireMockServer wireMockServer(){
            log.debug("<<< Creating WireMock Server Instance >>>");
            WireMockServer server = with(wireMockConfig().port(8083));
            server.start();
            return server;
        }

    }

    @BeforeEach
    void setUp() {
        customer = customerRepository.save(Customer.builder().customerName("Test Customer").build());
    }

    @Test
    void testNewToAllocated() throws JsonProcessingException {

        OilDto oilDto = OilDto.builder().id(oilId).productCode("123").build();
        //OilPagedList oilOrderPagedList = new OilPagedList(Arrays.asList(oilDto));
        wireMockServer.stubFor(get(OilServiceImpl.OIL_PRODUCTCODE_PATH_V1 + "123").willReturn(okJson(objectMapper.writeValueAsString(oilDto))));
        OilOrder oilOrder = createOilOrder();
        OilOrder savedOilOrder = oilOrderManager.newOilOrder(oilOrder);
        //Thread.sleep(5000); // Use Awaitility instead of sleep

        await().untilAsserted(() -> {
            OilOrder foundOrder = oilOrderRepository.findById(oilOrder.getId()).get();
            assertEquals(OilOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        assertNotNull(savedOilOrder);
    }

    @Test
    void testNewToValidationException() throws JsonProcessingException {

        OilDto oilDto = OilDto.builder().id(oilId).productCode("123").build();
        //OilPagedList oilOrderPagedList = new OilPagedList(Arrays.asList(oilDto));
        wireMockServer.stubFor(get(OilServiceImpl.OIL_PRODUCTCODE_PATH_V1 + "123").willReturn(okJson(objectMapper.writeValueAsString(oilDto))));
        OilOrder oilOrder = createOilOrder();
        oilOrder.setCustomerRef("pending-validation");
        OilOrder savedOilOrder = oilOrderManager.newOilOrder(oilOrder);
        //Thread.sleep(5000); // Use Awaitility instead of sleep

        await().untilAsserted(() -> {
            OilOrder foundOrder = oilOrderRepository.findById(oilOrder.getId()).get();
            assertEquals(OilOrderStatusEnum.VALIDATION_EXCEPTION, foundOrder.getOrderStatus());
        });

        assertNotNull(savedOilOrder);
    }

    @Test
    void testNewToPendingInventory() throws JsonProcessingException {

        OilDto oilDto = OilDto.builder().id(oilId).productCode("123").build();
        //OilPagedList oilOrderPagedList = new OilPagedList(Arrays.asList(oilDto));
        wireMockServer.stubFor(get(OilServiceImpl.OIL_PRODUCTCODE_PATH_V1 + "123").willReturn(okJson(objectMapper.writeValueAsString(oilDto))));
        OilOrder oilOrder = createOilOrder();
        oilOrder.setCustomerRef("pending-inventory");
        OilOrder savedOilOrder = oilOrderManager.newOilOrder(oilOrder);
        //Thread.sleep(5000); // Use Awaitility instead of sleep

        await().untilAsserted(() -> {
            OilOrder foundOrder = oilOrderRepository.findById(oilOrder.getId()).get();
            assertEquals(OilOrderStatusEnum.PENDING_INVENTORY, foundOrder.getOrderStatus());
        });

        assertNotNull(savedOilOrder);
    }

    @Test
    void testNewToAllocationException() throws JsonProcessingException {

        OilDto oilDto = OilDto.builder().id(oilId).productCode("123").build();
        //OilPagedList oilOrderPagedList = new OilPagedList(Arrays.asList(oilDto));
        wireMockServer.stubFor(get(OilServiceImpl.OIL_PRODUCTCODE_PATH_V1 + "123").willReturn(okJson(objectMapper.writeValueAsString(oilDto))));
        OilOrder oilOrder = createOilOrder();
        oilOrder.setCustomerRef("allocation-exception");
        OilOrder savedOilOrder = oilOrderManager.newOilOrder(oilOrder);
        //Thread.sleep(5000); // Use Awaitility instead of sleep

        await().untilAsserted(() -> {
            OilOrder foundOrder = oilOrderRepository.findById(oilOrder.getId()).get();
            assertEquals(OilOrderStatusEnum.ALLOCATION_EXCEPTION, foundOrder.getOrderStatus());
        });

        assertNotNull(savedOilOrder);
    }

    @Test
    void testNewToPickedUp() throws JsonProcessingException {

        OilDto oilDto = OilDto.builder().id(oilId).productCode("123").build();
        //OilPagedList oilOrderPagedList = new OilPagedList(Arrays.asList(oilDto));
        wireMockServer.stubFor(get(OilServiceImpl.OIL_PRODUCTCODE_PATH_V1 + "123").willReturn(okJson(objectMapper.writeValueAsString(oilDto))));
        OilOrder oilOrder = createOilOrder();
        OilOrder savedOilOrder = oilOrderManager.newOilOrder(oilOrder);
        //Thread.sleep(5000); // Use Awaitility instead of sleep

        await().untilAsserted(() -> {
            OilOrder foundOrder = oilOrderRepository.findById(oilOrder.getId()).get();
            assertEquals(OilOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        oilOrderManager.pickupOrder(oilOrder.getId());

        await().untilAsserted(() -> {
            OilOrder pickedOrder = oilOrderRepository.findById(oilOrder.getId()).get();
            assertEquals(OilOrderStatusEnum.PICKED_UP, pickedOrder.getOrderStatus());
        });

        assertNotNull(savedOilOrder);
    }

    @Test
    void testNewToCancelled() throws JsonProcessingException {

        OilDto oilDto = OilDto.builder().id(oilId).productCode("123").build();
        //OilPagedList oilOrderPagedList = new OilPagedList(Arrays.asList(oilDto));
        wireMockServer.stubFor(get(OilServiceImpl.OIL_PRODUCTCODE_PATH_V1 + "123").willReturn(okJson(objectMapper.writeValueAsString(oilDto))));
        OilOrder oilOrder = createOilOrder();
        OilOrder savedOilOrder = oilOrderManager.newOilOrder(oilOrder);
        //Thread.sleep(5000); // Use Awaitility instead of sleep

        await().untilAsserted(() -> {
            OilOrder foundOrder = oilOrderRepository.findById(oilOrder.getId()).get();
            assertEquals(OilOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        oilOrderManager.cancelOrder(oilOrder.getId());

        await().untilAsserted(() -> {
            OilOrder cancelledOrder = oilOrderRepository.findById(oilOrder.getId()).get();
            assertEquals(OilOrderStatusEnum.CANCELLED, cancelledOrder.getOrderStatus());
        });

        assertNotNull(savedOilOrder);
    }

    private OilOrder createOilOrder() {
        OilOrder oilOrder = OilOrder.builder()
                .customer(customer).build();

        Set<OilOrderLine> lines = new HashSet<>();
        lines.add(OilOrderLine.builder()
                .oilId(oilId)
                .productCode("123")
                .orderQuantity(1)
                .oilOrder(oilOrder)
                    .build());

        oilOrder.setOilOrderLines(lines);
        return oilOrder;
    }
}
