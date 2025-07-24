package com.personal.delivery_allocation_engine.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreateRequest {
    @NotNull
    private Long restaurantId;
    @NotNull
    private String restaurantLocation;
    @NotNull
    private String customerLocation;
} 