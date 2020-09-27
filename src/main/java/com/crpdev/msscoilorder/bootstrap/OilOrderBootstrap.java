package com.crpdev.msscoilorder.bootstrap;

import com.crpdev.msscoilorder.domain.Customer;
import com.crpdev.msscoilorder.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class OilOrderBootstrap implements CommandLineRunner {

    public static final String DEMO_ROOM = "Demo Room";
    public static final String OIL_PRODUCT_CODE_1 = "8005235079489";
    public static final String OIL_PRODUCT_CODE_2 = "4987176014894";
    public static final String OIL_PRODUCT_CODE_3 = "4987176014893";

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        Optional<Customer> customerOptional = customerRepository.findByCustomerName(DEMO_ROOM);

        if (!customerOptional.isPresent()) {
            //create if not found
            Customer savedCustomer = customerRepository.save(Customer.builder()
                    .customerName(DEMO_ROOM)
                    .apiKey(UUID.randomUUID())
                    .build());

            log.info("##################################################################");
            log.info("# Saved Customer Id: " + savedCustomer.getId()  + "#");
            log.info("##################################################################");
        } else {
            log.info("##################################################################");
            log.info("# Found Customer Id: " + customerOptional.get().getId() + "#");
            log.info("##################################################################");
        }
    }
}
