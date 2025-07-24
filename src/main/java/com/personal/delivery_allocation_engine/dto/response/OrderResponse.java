package com.personal.delivery_allocation_engine.dto.response;

import com.personal.delivery_allocation_engine.entity.Partner;
import com.personal.delivery_allocation_engine.entity.User;
import com.personal.delivery_allocation_engine.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {
  private Long id;
  private String restaurant;
  private String restaurantLocation;
  private User user;
  private OrderStatus status;
  private Partner partner;
  private Integer estimatedCookTime;
  private Integer totalEstimatedTime;
  private LocalDateTime timestamp;
} 