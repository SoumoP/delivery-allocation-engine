package com.personal.delivery_allocation_engine.service;

import com.personal.delivery_allocation_engine.entity.Partner;
import com.personal.delivery_allocation_engine.entity.Order;
import com.personal.delivery_allocation_engine.enums.OrderStatus;
import com.personal.delivery_allocation_engine.enums.PartnerStatus;
import com.personal.delivery_allocation_engine.exception.OrderNotFoundException;
import com.personal.delivery_allocation_engine.exception.PartnerNotFoundException;
import com.personal.delivery_allocation_engine.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerAllocationService {

  private final PartnerService partnerService;

  private final OrderRepository orderRepository;

  private final RoutingService routingService;

  @Async("partnerAllocationExecutor")
  @Retryable(value = {
      PartnerNotFoundException.class }, maxAttempts = 5, backoff = @Backoff(delay = 30000,  // 30 seconds initial delay
      multiplier = 1.5,  // Exponential backoff
      maxDelay = 300000  // Max 5 minutes delay
  ))
  public void allocateDeliveryPartnerAsync(Order order) {
    if (Objects.isNull(order)) {
      throw new OrderNotFoundException();
    }
    Long orderId = order.getId();
    log.info("Attempting to allocate delivery partner for order {}", orderId);

    // Check if order is still pending allocation
    if (order.getStatus() != OrderStatus.PENDING) {
      log.info("Order {} is no longer pending allocation, current status: {}", orderId, order.getStatus());
      return;
    }

    // Find and assign best partner
    Optional<Partner> bestPartner = partnerService.findBestPartner(order);

    if (bestPartner.isPresent()) {
      Partner partner = bestPartner.get();

      // Double-check partner availability before assignment
      if (!partnerService.isPartnerAvailable(partner)) {
        log.warn("Partner {} became unavailable during allocation", partner.getId());
        throw new PartnerNotFoundException("Selected partner became unavailable");
      }

      order.setPartner(partner);
      order.setStatus(OrderStatus.ASSIGNED);

      int eta = routingService.cacheETA(order);

      // Update partner status atomically
      partnerService.updatePartnerStatus(partner, PartnerStatus.BUSY);
      orderRepository.save(order);

      log.info("Order {} assigned to partner {} with estimated time {} minutes", orderId, partner.getId(), eta);
    } else {
      log.warn("No available partners found for order {} - attempt will be retried", orderId);
      throw new PartnerNotFoundException("No available delivery partners found");
    }
  }

  @Recover
  public CompletableFuture<Partner> recoverPartnerAllocation(PartnerNotFoundException ex, Long orderId) {
    log.error("Failed to allocate delivery partner for order {} after all retry attempts", orderId);

    // Update order status to failed or move to manual assignment queue
    Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

    order.setStatus(OrderStatus.ALLOCATION_FAILED);
    orderRepository.save(order);

    //notificationService.notifyAllocationFailure(order);

    return CompletableFuture.completedFuture(null);
  }
}
