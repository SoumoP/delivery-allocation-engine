package com.personal.delivery_allocation_engine.repository;

import com.personal.delivery_allocation_engine.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
} 