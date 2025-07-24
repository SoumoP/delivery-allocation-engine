package com.personal.delivery_allocation_engine.exception;

import com.personal.delivery_allocation_engine.dto.response.GenericResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<GenericResponseDTO<Void>> handleOrderNotFound(OrderNotFoundException ex) {
    log.error("Order not found: {}", ex.getMessage());

    GenericResponseDTO<Void> response = GenericResponseDTO.failure(ex.getMessage(), 404);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(PartnerNotFoundException.class)
  public ResponseEntity<GenericResponseDTO<Void>> handlePartnerNotFound(PartnerNotFoundException ex) {
    log.error("Partner not found: {}", ex.getMessage());

    GenericResponseDTO<Void> response = GenericResponseDTO.failure(
        "Unable to find available delivery partners at this time. Please try again later.", 503);
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
  }

  @ExceptionHandler(RestaurantNotFoundException.class)
  public ResponseEntity<GenericResponseDTO<Void>> handleRestaurantNotFound(RestaurantNotFoundException ex) {
    log.error("Restaurant not found: {}", ex.getMessage());

    GenericResponseDTO<Void> response = GenericResponseDTO.failure(ex.getMessage(), 404);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<GenericResponseDTO<Void>> handleUserNotFound(UserNotFoundException ex) {
    log.error("User not found: {}", ex.getMessage());

    GenericResponseDTO<Void> response = GenericResponseDTO.failure(ex.getMessage(), 404);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<GenericResponseDTO<List<String>>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    log.error("Validation failed: {}", ex.getMessage());

    List<String> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.toList());

    GenericResponseDTO<List<String>> response = GenericResponseDTO.failure("Validation failed", 400);
    response.setData(errors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<GenericResponseDTO<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
    log.error("Data integrity violation: {}", ex.getMessage());

    String message = ex.getMessage().contains("duplicate key") ?
        "Resource already exists" :
        "Data integrity constraint violated";

    GenericResponseDTO<Void> response = GenericResponseDTO.failure(message, 409);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<GenericResponseDTO<Void>> handleIllegalArgument(IllegalArgumentException ex) {
    log.error("Illegal argument: {}", ex.getMessage());

    GenericResponseDTO<Void> response = GenericResponseDTO.badRequest(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<GenericResponseDTO<Void>> handleGenericException(Exception ex) {
    log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

    GenericResponseDTO<Void> response = GenericResponseDTO.failure(
        "An unexpected error occurred. Please try again later.", 500);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}