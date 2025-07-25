package com.personal.delivery_allocation_engine.service;

import com.personal.delivery_allocation_engine.dao.ItemDao;
import com.personal.delivery_allocation_engine.entity.Item;
import com.personal.delivery_allocation_engine.entity.User;
import com.personal.delivery_allocation_engine.enums.OrderStatus;
import com.personal.delivery_allocation_engine.dto.request.OrderCreateRequest;
import com.personal.delivery_allocation_engine.dto.response.OrderResponse;
import com.personal.delivery_allocation_engine.entity.Order;
import com.personal.delivery_allocation_engine.entity.Restaurant;
import com.personal.delivery_allocation_engine.dao.OrderDao;
import com.personal.delivery_allocation_engine.exception.ItemNotFoundException;
import com.personal.delivery_allocation_engine.exception.OrderNotFoundException;
import com.personal.delivery_allocation_engine.utils.OrderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
  private final ItemDao itemDao;

  @Transactional
  public OrderResponse createOrder(OrderCreateRequest request) {
    log.info("Creating new order for restaurant {}, items : {}, user : {}", request.getRestaurantId(),
        request.getItemIds(), request.getUserId());

    // Validate restaurant exists
    Restaurant restaurant = restaurantService.getRestaurant(request.getRestaurantId());

    // Validate user exists
    User user = userService.getUser(request.getUserId());

    // Validate item exists
    List<Item> items = itemDao.findAllByIdIn(request.getItemIds());

    if (CollectionUtils.isEmpty(items)) {
      log.error("No items found for the provided item IDs: {}", request.getItemIds());
      throw new ItemNotFoundException();
    }
    // Create Order
    Order order = persistOrder(restaurant, user, items);

    // Trigger async partner allocation
    partnerAllocationService.allocateDeliveryPartnerAsync(order);

    return OrderUtils.mapToResponse(order);
  }

  private Order persistOrder(Restaurant restaurant, User user, List<Item> items) {
    Order order = Order.builder().restaurant(restaurant).user(user).items(items).status(OrderStatus.PENDING).build();
    return orderDao.save(order);
  }

  public OrderResponse getOrder(Long orderId) {
    Order order = orderDao.findById(orderId);
    return OrderUtils.mapToResponse(order);
  }

  public List<OrderResponse> getPartnerOrders(Long partnerId) {
    List<Order> orders = orderDao.findByAssignedPartnerIdAndStatus(partnerId, OrderStatus.ASSIGNED);
    return orders.stream().map(OrderUtils::mapToResponse)
        .sorted(Comparator.comparing(OrderResponse::getTimestamp).reversed()).toList();
  }

  public List<OrderResponse> getUserOrders(Long userId) {
    List<Order> orders = orderDao.findAllByUserId(userId);
    return orders.stream().map(OrderUtils::mapToResponse)
        .sorted(Comparator.comparing(OrderResponse::getTimestamp).reversed()).toList();
  }
}