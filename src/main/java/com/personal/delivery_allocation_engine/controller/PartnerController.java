package com.personal.delivery_allocation_engine.controller;

import com.personal.delivery_allocation_engine.dto.request.LocationUpdateRequest;
import com.personal.delivery_allocation_engine.dto.response.PartnerLocationResponse;
import com.personal.delivery_allocation_engine.dao.PartnerDao;
import com.personal.delivery_allocation_engine.entity.Partner;
import com.personal.delivery_allocation_engine.service.PartnerLocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * @author Soumyajit Podder created on 23/07/25
 */
@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
@Tag(name = "Partners", description = "Delivery partner management APIs")
public class PartnerController {

  private final PartnerDao partnerDao;
  private final PartnerLocationService partnerLocationService;

  @PostMapping("/{id}/location")
  @Operation(summary = "Update partner location", description = "Updates the real-time GPS coordinates of a delivery partner")
  public ResponseEntity<PartnerLocationResponse> updateLocation(
      @Parameter(description = "Partner ID") @PathVariable Long id, @Valid @RequestBody LocationUpdateRequest request) {
    PartnerLocationResponse response = partnerLocationService.updatePartnerLocation(id, request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get partner by ID", description = "Retrieves a specific delivery partner by ID")
  public ResponseEntity<Partner> getPartner(@Parameter(description = "Partner ID") @PathVariable Long id) {
    Partner partner = partnerDao.getPartner(id);
    return ResponseEntity.ok(partner);
  }
}