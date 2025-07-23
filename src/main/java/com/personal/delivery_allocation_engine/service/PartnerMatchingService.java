package com.personal.delivery_allocation_engine.service;

import com.personal.delivery_allocation_engine.model.DeliveryPartner;
import com.personal.delivery_allocation_engine.model.Order;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PartnerMatchingService {
    public Optional<DeliveryPartner> findBestPartner(Order order) {
        // TODO: Implement partner matching logic
        return Optional.empty();
    }
} 