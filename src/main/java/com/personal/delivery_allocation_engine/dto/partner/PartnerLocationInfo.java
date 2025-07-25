package com.personal.delivery_allocation_engine.dto.partner;

import com.personal.delivery_allocation_engine.enums.PartnerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartnerLocationInfo {
  private Long partnerId;
  private double latitude;
  private double longitude;
  private double distanceMeters;
  private PartnerStatus status;
  private LocalDateTime lastUpdated;

  public double getDistanceKm() {
    return distanceMeters / 1000.0;
  }
}

