package com.personal.delivery_allocation_engine.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long restaurantId;
    private String restaurantLocation;
    private String customerLocation;
    private String status;
    private Long assignedPartnerId;
    private Integer estimatedCookTime;
    private Integer totalEstimatedTime;
    private LocalDateTime timestamp;
} 