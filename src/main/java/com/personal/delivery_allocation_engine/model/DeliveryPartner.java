package com.personal.delivery_allocation_engine.model;

import lombok.Data;

@Data
public class DeliveryPartner {
    private Long id;
    private String name;
    private String currentLocation;
    private PartnerStatus status;

    public enum PartnerStatus {
        AVAILABLE, BUSY
    }
} 