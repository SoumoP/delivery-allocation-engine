package com.personal.delivery_allocation_engine.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {
    @NotNull
    private Long restaurantId;
    @NotNull
    private List<Long> itemIds;
    @NotNull
    private Long userId;
} 