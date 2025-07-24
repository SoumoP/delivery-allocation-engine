package com.personal.delivery_allocation_engine.exception;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super(message);
  }

  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserNotFoundException() {
    super("User not found");
  }
}
