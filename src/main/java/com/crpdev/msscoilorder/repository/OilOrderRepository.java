package com.crpdev.msscoilorder.repository;

import com.crpdev.msscoilorder.domain.Customer;
import com.crpdev.msscoilorder.domain.OilOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

@Repository
public interface OilOrderRepository extends JpaRepository<OilOrder, UUID> {

    Page<OilOrder> findAllByCustomer(Customer customer, Pageable pageable);

    List<OilOrder> findAllByOrderStatus(String orderStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    OilOrder findOneById(UUID id);
}
