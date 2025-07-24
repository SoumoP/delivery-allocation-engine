package com.personal.delivery_allocation_engine.exception;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
public class PartnerNotFoundException extends RuntimeException {
  public PartnerNotFoundException(String message) {
    super(message);
  }
}
