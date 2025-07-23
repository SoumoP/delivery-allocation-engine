package com.personal.delivery_allocation_engine.service;

import com.personal.delivery_allocation_engine.dto.OrderCreateRequest;
import com.personal.delivery_allocation_engine.dto.OrderResponse;
import com.personal.delivery_allocation_engine.exception.RestaurantNotFoundException;
import com.personal.delivery_allocation_engine.model.DeliveryPartner;
import com.personal.delivery_allocation_engine.model.Order;
import com.personal.delivery_allocation_engine.model.Restaurant;
import com.personal.delivery_allocation_engine.repository.OrderRepository;
import com.personal.delivery_allocation_engine.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Soumyajit Podder created on 23/07/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

  private final OrderRepository orderRepository;
  private final RestaurantRepository restaurantRepository;
  private final PartnerMatchingService partnerMatchingService;
  private final PartnerService partnerService;
  private final RoutingService routingService;

  @Transactional
  public OrderResponse createOrder(OrderCreateRequest request) {
    log.info("Creating new order for restaurant {}", request.getRestaurantId());

    // Validate restaurant exists
    Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId()).orElseThrow(
        () -> new RestaurantNotFoundException("Restaurant not found with id: " + request.getRestaurantId()));

    // Create order
    Order order = Order.builder().restaurantId(request.getRestaurantId())
        .restaurantLocation(request.getRestaurantLocation()).customerLocation(request.getCustomerLocation())
        .timestamp(LocalDateTime.now()).status(Order.OrderStatus.PENDING).build();

    order = orderRepository.save(order);

    // Find and assign best partner
    Optional<DeliveryPartner> bestPartner = partnerMatchingService.findBestPartner(order);

    if (bestPartner.isPresent()) {
      DeliveryPartner partner = bestPartner.get();
      order.setAssignedPartnerId(partner.getId());
      order.setStatus(Order.OrderStatus.ASSIGNED);

      // Calculate total estimated time
      int etaToRestaurant = routingService.getEstimatedTravelTime(partner.getCurrentLocation(),
          order.getRestaurantLocation());
      int etaToCustomer = routingService.getEstimatedTravelTime(order.getRestaurantLocation(),
          order.getCustomerLocation());
      int cookTime = order.getEstimatedCookTime();

      order.setTotalEstimatedTime(Math.max(etaToRestaurant, cookTime) + etaToCustomer);

      // Update partner status
      partnerService.updatePartnerStatus(partner.getId(), DeliveryPartner.PartnerStatus.BUSY);

      log.info("Order {} assigned to partner {} with estimated time {} minutes", order.getId(), partner.getId(),
          order.getTotalEstimatedTime());
    } else {
      log.warn("No available partners found for order {}", order.getId());
    }

    order = orderRepository.save(order);
    return mapToResponse(order);
  }

  public List<OrderResponse> getAllOrders() {
    return orderRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
  }

  public OrderResponse getOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    return mapToResponse(order);
  }

  @Transactional
  public OrderResponse updateOrderStatus(Long orderId, Order.OrderStatus status) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

    Order.OrderStatus previousStatus = order.getStatus();
    order.setStatus(status);
    order = orderRepository.save(order);

    // Handle status transitions
    if (status == Order.OrderStatus.DELIVERED && order.getAssignedPartnerId() != null) {
      // Free up the partner
      partnerService.updatePartnerStatus(order.getAssignedPartnerId(), DeliveryPartner.PartnerStatus.AVAILABLE);
      log.info("Order {} delivered, partner {} is now available", orderId, order.getAssignedPartnerId());
    }

    log.info("Order {} status updated from {} to {}", orderId, previousStatus, status);
    return mapToResponse(order);
  }

  public List<OrderResponse> getPartnerOrders(Long partnerId) {
    List<Order> orders = orderRepository.findByAssignedPartnerIdAndStatus(partnerId, Order.OrderStatus.ASSIGNED);
    return orders.stream().map(this::mapToResponse).collect(Collectors.toList());
  }

  private OrderResponse mapToResponse(Order order) {
    return OrderResponse.builder().id(order.getId()).restaurantId(order.getRestaurantId())
        .restaurantLocation(order.getRestaurantLocation()).customerLocation(order.getCustomerLocation())
        .status(order.getStatus()).assignedPartnerId(order.getAssignedPartnerId())
        .estimatedCookTime(order.getEstimatedCookTime()).totalEstimatedTime(order.getTotalEstimatedTime())
        .timestamp(order.getTimestamp()).build();
  }
}