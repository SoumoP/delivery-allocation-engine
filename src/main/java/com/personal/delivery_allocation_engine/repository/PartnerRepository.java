package com.personal.delivery_allocation_engine.repository;

import com.personal.delivery_allocation_engine.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
  List<Partner> findAllByIdIn(List<Long> partnerIds);
} 