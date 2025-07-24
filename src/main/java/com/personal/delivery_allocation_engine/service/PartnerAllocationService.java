package com.personal.delivery_allocation_engine.service;

import com.personal.delivery_allocation_engine.dao.OrderDao;
import com.personal.delivery_allocation_engine.entity.Partner;
import com.personal.delivery_allocation_engine.entity.Order;
import com.personal.delivery_allocation_engine.enums.OrderStatus;
import com.personal.delivery_allocation_engine.enums.PartnerStatus;
import com.personal.delivery_allocation_engine.exception.OrderNotFoundException;
import com.personal.delivery_allocation_engine.exception.PartnerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerAllocationService {

  private final PartnerDao partnerDao;
  private final OrderDao orderDao;

  private final RoutingService routingService;

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

    Optional<Partner> maybePartner = findBestPartner(order);

    if (maybePartner.isEmpty()) {
      log.warn("No available partners found for order {} - attempt will be retried", orderId);
      throw new PartnerNotFoundException("No available delivery partners found.");
    }

    Partner partner = maybePartner.get();

    if (!partnerDao.isPartnerAvailable(partner)) {
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
    Order order = orderDao.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

    order.setStatus(OrderStatus.ALLOCATION_FAILED);
    orderDao.save(order);

    //notificationService.notifyAllocationFailure(order);

    return CompletableFuture.completedFuture(null);
  }

  public Optional<Partner> findBestPartner(Order order) {
    return Optional.empty();
  }
}
