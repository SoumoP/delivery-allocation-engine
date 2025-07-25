package com.personal.delivery_allocation_engine.exception;

/**
 * @author Soumyajit Podder created on 25/07/25
 */
public class ItemNotFoundException extends RuntimeException {
  public ItemNotFoundException() {
    super("Item not found");
  }

  public ItemNotFoundException(Long id) {
    super("Item not found with ID : " + id);
  }
}
