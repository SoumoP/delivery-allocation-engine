package com.personal.delivery_allocation_engine.dto.partner;

import com.personal.delivery_allocation_engine.entity.Partner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerCandidate {
  private Partner partner;
  private PartnerLocationInfo locationInfo;
  private double restaurantDistance;
  private double deliveryDistance;
  private double totalDistance;
  private double totalScore;

  public double getEstimatedPickupTimeMinutes() {
    // Assume average speed of 30 km/h for pickup
    return (restaurantDistance / 30.0) * 60;
  }

  public double getEstimatedDeliveryTimeMinutes() {
    // Assume average speed of 25 km/h for delivery (with traffic)
    return (deliveryDistance / 25.0) * 60;
  }

  public double getTotalEstimatedTimeMinutes() {
    return getEstimatedPickupTimeMinutes() + getEstimatedDeliveryTimeMinutes();
  }
}
