package com.crpdev.msscoilorder.services;

import com.crpdev.factory.oil.model.oil.OilDto;
import com.crpdev.msscoilorder.domain.Customer;
import com.crpdev.msscoilorder.domain.OilOrder;
import com.crpdev.msscoilorder.domain.OilOrderLine;
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
    void testNewToAllocated() throws JsonProcessingException, InterruptedException {

        OilDto oilDto = OilDto.builder().id(oilId).productCode("123").build();
        //OilPagedList oilOrderPagedList = new OilPagedList(Arrays.asList(oilDto));
        wireMockServer.stubFor(get(OilServiceImpl.OIL_PRODUCTCODE_PATH_V1 + "123").willReturn(okJson(objectMapper.writeValueAsString(oilDto))));
        OilOrder oilOrder = createOilOrder();
        Thread.sleep(5000);
        OilOrder savedOilOrder = oilOrderManager.newOilOrder(oilOrder);
        assertNotNull(savedOilOrder);
        //assertEquals(OilOrderStatusEnum.ALLOCATED, savedOilOrder.getOrderStatus());
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
