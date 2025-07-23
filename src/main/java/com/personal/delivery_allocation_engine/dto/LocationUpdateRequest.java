package com.personal.delivery_allocation_engine.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LocationUpdateRequest {
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
} 