package com.personal.delivery_allocation_engine.service;

import com.personal.delivery_allocation_engine.entity.User;
import com.personal.delivery_allocation_engine.enums.OrderStatus;
import com.personal.delivery_allocation_engine.dto.request.OrderCreateRequest;
import com.personal.delivery_allocation_engine.dto.response.OrderResponse;
import com.personal.delivery_allocation_engine.entity.Order;
import com.personal.delivery_allocation_engine.entity.Restaurant;
import com.personal.delivery_allocation_engine.dao.OrderDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Soumyajit Podder created on 23/07/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
  private final PartnerAllocationService partnerAllocationService;
  private final UserService userService;
  private final RestaurantService restaurantService;
  private final OrderDao orderDao;

  @Transactional
  public OrderResponse createOrder(OrderCreateRequest request) {
    log.info("Creating new order for restaurant {}", request.getRestaurantId());

    // Validate restaurant exists
    Restaurant restaurant = restaurantService.getRestaurant(request);

    // Validate user exists
    User user = userService.getUser(request);

    // Create Order
    Order order = persistOrder(restaurant, user);

    // Trigger async partner allocation
    partnerAllocationService.allocateDeliveryPartnerAsync(order);

    return mapToResponse(order);
  }

  private Order persistOrder(Restaurant restaurant, User user) {
    Order order = Order.builder().restaurant(restaurant).user(user).status(OrderStatus.PENDING).build();
    return orderDao.save(order);
  }

  public List<OrderResponse> getAllOrders() {
    return orderDao.findAll().stream().map(this::mapToResponse).toList();
  }

  public OrderResponse getOrder(Long orderId) {
    Order order = orderDao.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    return mapToResponse(order);
  }

  public List<OrderResponse> getPartnerOrders(Long partnerId) {
    List<Order> orders = orderDao.findByAssignedPartnerIdAndStatus(partnerId, OrderStatus.ASSIGNED);
    return orders.stream().map(this::mapToResponse).toList();
  }

  private OrderResponse mapToResponse(Order order) {
    return OrderResponse.builder().id(order.getId()).restaurant(order.getRestaurant().getName())
        .restaurantLocation(order.getRestaurant().getLat() + "," + order.getRestaurant().getLng())
        .customerLocation(order.getUser().getLat() + "," + order.getUser().getLng()).status(order.getStatus())
        .assignedPartnerId(null).estimatedCookTime(null).totalEstimatedTime(null).timestamp(order.getCreatedAt())
        .build();
  }
}