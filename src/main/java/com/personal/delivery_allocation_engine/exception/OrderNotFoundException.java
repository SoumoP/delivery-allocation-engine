package com.personal.delivery_allocation_engine.exception;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
public class OrderNotFoundException extends RuntimeException {
  public OrderNotFoundException() {
    super("Order not found");
  }
  public OrderNotFoundException(Long id) {
    super("Order not found with ID : " + id);
  }
}