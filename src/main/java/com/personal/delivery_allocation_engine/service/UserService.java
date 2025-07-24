package com.personal.delivery_allocation_engine.service;

import com.personal.delivery_allocation_engine.dto.request.OrderCreateRequest;
import com.personal.delivery_allocation_engine.entity.User;
import com.personal.delivery_allocation_engine.exception.UserNotFoundException;
import com.personal.delivery_allocation_engine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public User getUser(OrderCreateRequest request) {
    return userRepository.findById(request.getRestaurantId())
        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getRestaurantId()));
  }
}
