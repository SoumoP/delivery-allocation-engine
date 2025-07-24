package com.personal.delivery_allocation_engine.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartnerLocationResponse {
    private Long id;
    private String name;
    private String currentLocation;
    private String status;
} 