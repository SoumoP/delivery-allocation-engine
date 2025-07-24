package com.personal.delivery_allocation_engine.utils;

import com.personal.delivery_allocation_engine.dto.response.OrderResponse;
import com.personal.delivery_allocation_engine.entity.Order;
import lombok.experimental.UtilityClass;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@UtilityClass
public class OrderUtils {
  public OrderResponse mapToResponse(Order order) {
    return OrderResponse.builder().id(order.getId()).restaurant(order.getRestaurant().getName())
        .restaurantLocation(order.getRestaurant().getLat() + "," + order.getRestaurant().getLng()).user(order.getUser())
        .status(order.getStatus()).partner(order.getPartner()).estimatedCookTime(null).totalEstimatedTime(null)
        .timestamp(order.getCreatedAt()).build();
  }
}
