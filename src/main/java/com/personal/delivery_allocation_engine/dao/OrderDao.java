package com.personal.delivery_allocation_engine.dao;

import com.personal.delivery_allocation_engine.entity.Order;
import com.personal.delivery_allocation_engine.enums.OrderStatus;
import com.personal.delivery_allocation_engine.exception.OrderNotFoundException;
import com.personal.delivery_allocation_engine.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@Service
@RequiredArgsConstructor
public class OrderDao {
  private final OrderRepository orderRepository;

  public Order save(Order order) {
    return orderRepository.save(order);
  }

  public Order findById(Long orderId) {
    return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
  }

  public List<Order> findByAssignedPartnerIdAndStatus(Long partnerId, OrderStatus orderStatus) {
    return orderRepository.findByAssignedPartnerIdAndStatus(partnerId, orderStatus);
  }

  public List<Order> findAllByUserId(Long userId) {
    return orderRepository.findAllByUser_Id(userId);
  }
}
