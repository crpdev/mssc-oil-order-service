package com.crpdev.msscoilorder.service.oil;

import com.crpdev.factory.oil.model.oil.OilDto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@ConfigurationProperties(prefix = "crp.factory", ignoreUnknownFields = false)
@Service
public class OilServiceImpl implements OilService {

    public static final String OIL_PATH_V1 = "/api/v1/oil/";
    public static final String OIL_PRODUCTCODE_PATH_V1 = "/api/v1/oil/productCode/";

    private final RestTemplate restTemplate;

    private String oilServiceHost;

    public void setOilServiceHost(String oilServiceHost) {
        this.oilServiceHost = oilServiceHost;
    }

    public OilServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Optional<OilDto> getOilById(UUID oilId) {
        return Optional.of(restTemplate.getForObject(oilServiceHost + OIL_PATH_V1 + oilId.toString(), OilDto.class));
    }

    @Override
    public Optional<OilDto> getOilByProductCode(String productCode) {
        return Optional.of(restTemplate.getForObject(oilServiceHost + OIL_PRODUCTCODE_PATH_V1 + productCode, OilDto.class));
    }
}
