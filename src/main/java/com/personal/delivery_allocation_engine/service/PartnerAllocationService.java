package com.personal.delivery_allocation_engine.service;

import com.personal.delivery_allocation_engine.dao.OrderDao;
import com.personal.delivery_allocation_engine.dao.PartnerDao;
import com.personal.delivery_allocation_engine.dto.partner.PartnerCandidate;
import com.personal.delivery_allocation_engine.dto.partner.PartnerLocationInfo;
import com.personal.delivery_allocation_engine.entity.Partner;
import com.personal.delivery_allocation_engine.entity.Order;
import com.personal.delivery_allocation_engine.entity.Restaurant;
import com.personal.delivery_allocation_engine.entity.User;
import com.personal.delivery_allocation_engine.enums.OrderStatus;
import com.personal.delivery_allocation_engine.enums.PartnerStatus;
import com.personal.delivery_allocation_engine.exception.OrderNotFoundException;
import com.personal.delivery_allocation_engine.exception.PartnerNotFoundException;
import com.personal.delivery_allocation_engine.utils.HaversineUtils;
import com.personal.delivery_allocation_engine.utils.PartnerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerAllocationService {

  private final PartnerDao partnerDao;
  private final OrderDao orderDao;
  private final RoutingService routingService;
  private final PartnerLocationService partnerLocationService;
  @Value("${delivery.allocation.base-search-radius-km:5}")
  private int baseSearchRadius;

  @Async("partnerAllocationExecutor")
  @Retryable(retryFor = PartnerNotFoundException.class, maxAttempts = 5, backoff = @Backoff(delay = 30000, // 30s initial delay
      multiplier = 1.5, // Exponential backoff
      maxDelay = 300000 // Max 5m delay
  ))
  public void allocateDeliveryPartnerAsync(final Order order) {
    if (order == null) {
      log.error("Order object is null. Cannot allocate partner.");
      throw new OrderNotFoundException();
    }

    final Long orderId = order.getId();
    log.info("Attempting to allocate delivery partner for order {}", orderId);

    if (order.getStatus() != OrderStatus.PENDING) {
      log.info("Order {} is no longer pending allocation. Current status: {}", orderId, order.getStatus());
      return;
    }

    int currentAttempt = getCurrentRetryCount();
    int searchRadius = baseSearchRadius + (currentAttempt - 1) * 2;

    Optional<Partner> maybePartner = findBestPartner(order, searchRadius, currentAttempt <= 2);

    if (maybePartner.isEmpty()) {
      log.warn("No available partners found for order {} - attempt will be retried", orderId);
      throw new PartnerNotFoundException("No available delivery partners found.");
    }

    Partner partner = maybePartner.get();

    if (!PartnerUtils.isPartnerAvailable(partner)) {
      log.warn("Partner {} became unavailable during allocation for order {}", partner.getId(), orderId);
      throw new PartnerNotFoundException("Selected partner became unavailable");
    }

    assignPartnerToOrder(order, partner);
  }

  private void assignPartnerToOrder(Order order, Partner partner) {
    int eta = routingService.cacheETA(order);
    order.setPartner(partner);
    order.setStatus(OrderStatus.ASSIGNED);

    partnerDao.updatePartnerStatus(partner, PartnerStatus.BUSY);
    orderDao.save(order);

    log.info("Order {} assigned to partner {} with estimated time {} minutes", order.getId(), partner.getId(), eta);
  }

  @Recover
  public CompletableFuture<Partner> recoverPartnerAllocation(PartnerNotFoundException ex, Long orderId) {
    log.error("Failed to allocate delivery partner for order {} after all retry attempts", orderId);

    // Update order status to failed or move to manual assignment queue
    Order order = orderDao.findById(orderId);

    order.setStatus(OrderStatus.ALLOCATION_FAILED);
    orderDao.save(order);

    //notificationService.notifyAllocationFailure(order);

    return CompletableFuture.completedFuture(null);
  }

  public Optional<Partner> findBestPartner(Order order, int searchRadiusKm, boolean onlyAvailable) {
    log.info("Finding best partner for order {} from restaurant {}", order.getId(), order.getRestaurant().getId());

    try {
      // Get restaurant and user locations
      Restaurant restaurant = order.getRestaurant();
      User user = order.getUser();

      // Find nearby partners
      List<PartnerLocationInfo> nearbyPartners = partnerLocationService.findPartnersNearRestaurant(restaurant.getLat(),
          restaurant.getLng(), searchRadiusKm);

      if (nearbyPartners.isEmpty()) {
        log.warn("No partners found within {} km of restaurant {}", searchRadiusKm, restaurant.getId());
        return Optional.empty();
      }

      // Get partner entities and filter available ones
      List<PartnerCandidate> candidates = buildPartnerCandidates(nearbyPartners, user, onlyAvailable);

      if (candidates.isEmpty()) {
        log.warn("No available partners found for order {}", order.getId());
        return Optional.empty();
      }

      // Score and rank partners
      PartnerCandidate bestCandidate = selectBestPartner(candidates, searchRadiusKm);

      log.info("Selected partner {} for order {} with score {}", bestCandidate.getPartner().getId(), order.getId(),
          bestCandidate.getTotalScore());

      return Optional.of(bestCandidate.getPartner());

    } catch (Exception e) {
      log.error("Error finding best partner for order {}: {}", order.getId(), e.getMessage(), e);
      return Optional.empty();
    }
  }

  private List<PartnerCandidate> buildPartnerCandidates(List<PartnerLocationInfo> nearbyPartners, User user,
      boolean onlyAvailable) {
    List<Long> partnerIds = nearbyPartners.stream().map(PartnerLocationInfo::getPartnerId).toList();

    // Fetch partner entities from database
    Map<Long, Partner> partnerMap = partnerDao.findAllById(partnerIds).stream()
        .collect(Collectors.toMap(Partner::getId, partner -> partner));

    List<PartnerCandidate> candidates = new ArrayList<>();

    for (PartnerLocationInfo locationInfo : nearbyPartners) {
      Partner partner = partnerMap.get(locationInfo.getPartnerId());

      if (partner != null && (!onlyAvailable || PartnerUtils.isPartnerAvailable(partner))) {
        // Calculate total delivery time and distance
        double restaurantToPartnerDistance = locationInfo.getDistanceKm();
        double partnerToUserDistance = HaversineUtils.getDistanceInMetres(locationInfo.getLatitude(),
            locationInfo.getLongitude(), user.getLat(), user.getLng());

        PartnerCandidate candidate = PartnerCandidate.builder().partner(partner).locationInfo(locationInfo)
            .restaurantDistance(restaurantToPartnerDistance).deliveryDistance(partnerToUserDistance)
            .totalDistance(restaurantToPartnerDistance + partnerToUserDistance).build();

        candidates.add(candidate);
      }
    }

    return candidates;
  }

  private PartnerCandidate selectBestPartner(List<PartnerCandidate> candidates, int searchRadiusKm) {
    // Score each candidate
    for (PartnerCandidate candidate : candidates) {
      double score = calculatePartnerScore(candidate, searchRadiusKm);
      candidate.setTotalScore(score);
    }

    // Sort by score (descending - higher is better)
    candidates.sort((a, b) -> Double.compare(b.getTotalScore(), a.getTotalScore()));

    return candidates.getFirst();
  }

  private double calculatePartnerScore(PartnerCandidate candidate, int searchRadiusKm) {
    // Scoring factors with weights
    double distanceScore = calculateDistanceScore(candidate.getTotalDistance(), searchRadiusKm);
    double performanceScore = candidate.getPartner().getPerformanceScore();
    double availabilityScore = calculateAvailabilityScore(candidate.getPartner());

    // Weighted final score
    return (distanceScore * 0.5) + (performanceScore * 0.3) + (availabilityScore * 0.2);
  }

  private double calculateDistanceScore(double totalDistanceKm, int searchRadiusKm) {
    // Score inversely proportional to distance
    // Normalize to 0-100 scale
    return Math.max(0, 100 - (totalDistanceKm / searchRadiusKm * 100));
  }

  private double calculateAvailabilityScore(Partner partner) {
    // Higher score for partners who are online and not busy
    return switch (partner.getStatus()) {
      case AVAILABLE -> 100.0;
      case BUSY -> 50.0; // Still available but handling another order
    };
  }

  private int getCurrentRetryCount() {
    RetryContext context = RetrySynchronizationManager.getContext();
    if (context != null) {
      return context.getRetryCount() + 1;
    }
    return 1;
  }
}
