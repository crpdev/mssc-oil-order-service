package com.crpdev.msscoilorder.repository;

import com.crpdev.msscoilorder.domain.OilOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OilOrderLineRepository extends JpaRepository<OilOrderLine, UUID> {
}
