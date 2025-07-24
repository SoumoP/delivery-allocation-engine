package com.personal.delivery_allocation_engine.service;

import com.personal.delivery_allocation_engine.dto.request.OrderCreateRequest;
import com.personal.delivery_allocation_engine.entity.Restaurant;
import com.personal.delivery_allocation_engine.exception.RestaurantNotFoundException;
import com.personal.delivery_allocation_engine.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@Service
@RequiredArgsConstructor
public class RestaurantService {
  private final RestaurantRepository restaurantRepository;

  public Restaurant getRestaurant(Long restaurantId) {
    return restaurantRepository.findById(restaurantId)
        .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + restaurantId));
  }
}
