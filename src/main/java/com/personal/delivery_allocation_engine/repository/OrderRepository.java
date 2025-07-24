package com.personal.delivery_allocation_engine.repository;

import com.personal.delivery_allocation_engine.entity.Order;
import com.personal.delivery_allocation_engine.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByAssignedPartnerIdAndStatus(Long partnerId, OrderStatus status);
} 