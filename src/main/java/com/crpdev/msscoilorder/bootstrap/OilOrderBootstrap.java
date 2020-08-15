package com.crpdev.msscoilorder.bootstrap;

import com.crpdev.msscoilorder.domain.Customer;
import com.crpdev.msscoilorder.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class OilOrderBootstrap implements CommandLineRunner {

    public static final String DEMO_ROOM = "Demo Room";
    public static final String OIL_BARCODE_1 = "8005235079489";
    public static final String OIL_BARCODE_2 = "4987176014894";
    public static final String OIL_BARCODE_3 = "4987176014893";

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        loadCustomerData();
    }

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            customerRepository.save(Customer.builder()
                    .customerName(DEMO_ROOM)
                    .apiKey(UUID.randomUUID())
                    .build());
        }
    }
}
