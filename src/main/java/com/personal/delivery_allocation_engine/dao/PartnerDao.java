package com.personal.delivery_allocation_engine.dao;

import com.personal.delivery_allocation_engine.dto.request.LocationUpdateRequest;
import com.personal.delivery_allocation_engine.dto.response.PartnerLocationResponse;
import com.personal.delivery_allocation_engine.entity.Partner;
import com.personal.delivery_allocation_engine.enums.PartnerStatus;
import com.personal.delivery_allocation_engine.exception.PartnerNotFoundException;
import com.personal.delivery_allocation_engine.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PartnerDao {
  private final PartnerRepository partnerRepository;

  public void updatePartnerStatus(Partner partner, PartnerStatus status) {
    partner.setStatus(status);
    partnerRepository.save(partner);
  }

  public Partner getPartner(Long id) {
    return partnerRepository.findById(id).orElseThrow(() -> new PartnerNotFoundException(id));
  }

  public List<Partner> findAllById(List<Long> partnerIds) {
    return partnerRepository.findAllByIdIn(partnerIds);
  }

}
