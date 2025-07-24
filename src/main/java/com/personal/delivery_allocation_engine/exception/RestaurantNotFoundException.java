package com.personal.delivery_allocation_engine.exception;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
public class RestaurantNotFoundException extends RuntimeException {

  public RestaurantNotFoundException(String message) {
    super(message);
  }

  public RestaurantNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public RestaurantNotFoundException() {
    super("Restaurant not found");
  }
}