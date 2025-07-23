package com.personal.delivery_allocation_engine.service;

import com.personal.delivery_allocation_engine.dto.LocationUpdateRequest;
import com.personal.delivery_allocation_engine.dto.PartnerLocationResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PartnerService {
    public void updatePartnerStatus(Long partnerId, String status) {
        // TODO: Implement status update logic
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
