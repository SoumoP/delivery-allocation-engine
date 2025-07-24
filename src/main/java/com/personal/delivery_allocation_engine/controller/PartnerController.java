package com.personal.delivery_allocation_engine.controller;

import com.personal.delivery_allocation_engine.dto.request.LocationUpdateRequest;
import com.personal.delivery_allocation_engine.dtoPartnerLocationResponse;
import com.personal.delivery_allocation_engine.service.PartnerService;
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

  private final PartnerService partnerService;

  @PostMapping("/{id}/location")
  @Operation(summary = "Update partner location", description = "Updates the real-time GPS coordinates of a delivery partner")
  public ResponseEntity<PartnerLocationResponse> updateLocation(
      @Parameter(description = "Partner ID") @PathVariable Long id, @Valid @RequestBody LocationUpdateRequest request) {
    PartnerLocationResponse response = partnerService.updateLocation(id, request);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  @Operation(summary = "Get all partners", description = "Retrieves all delivery partners and their current locations")
  public ResponseEntity<List<PartnerLocationResponse>> getAllPartners() {
    List<PartnerLocationResponse> partners = partnerService.getAllPartners();
    return ResponseEntity.ok(partners);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get partner by ID", description = "Retrieves a specific delivery partner by ID")
  public ResponseEntity<PartnerLocationResponse> getPartner(
      @Parameter(description = "Partner ID") @PathVariable Long id) {
    PartnerLocationResponse partner = partnerService.getPartner(id);
    return ResponseEntity.ok(partner);
  }
}