package com.personal.delivery_allocation_engine.service;

import com.personal.delivery_allocation_engine.dto.request.LocationUpdateRequest;
import com.personal.delivery_allocation_engine.dto.response.PartnerLocationResponse;
import com.personal.delivery_allocation_engine.entity.Partner;
import com.personal.delivery_allocation_engine.enums.PartnerStatus;
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

  public boolean isPartnerAvailable(Partner partner) {
    return Objects.nonNull(partner) && partner.getStatus() == PartnerStatus.AVAILABLE;
  }

  public PartnerLocationResponse updateLocation(Long id, LocationUpdateRequest request) {
    // TODO: Implement location update logic
    return PartnerLocationResponse.builder().id(id).currentLocation("0,0").status("AVAILABLE").build();
  }

  public List<PartnerLocationResponse> getAllPartners() {
    // TODO: Implement retrieval of all partners
    return Collections.emptyList();
  }

  public PartnerLocationResponse getPartner(Long id) {
    // TODO: Implement retrieval of a partner by id
    return PartnerLocationResponse.builder().id(id).currentLocation("0,0").status("AVAILABLE").build();
  }
}
