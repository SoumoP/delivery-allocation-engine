package com.personal.delivery_allocation_engine.dto.response;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponseDTO<T> {
  private boolean success;
  private String message;
  private int statusCode;
  private T data;
  private LocalDateTime timestamp;
  private String path;

  public static <T> GenericResponseDTO<T> success(T data) {
    return new GenericResponseDTO<>(true, "Operation successful", 200, data, LocalDateTime.now(), null);
  }

  public static <T> GenericResponseDTO<T> success(String message, T data) {
    return new GenericResponseDTO<>(true, message, 200, data, LocalDateTime.now(), null);
  }

  public static <T> GenericResponseDTO<T> failure(String message, int statusCode) {
    return new GenericResponseDTO<>(false, message, statusCode, null, LocalDateTime.now(), getCurrentPath());
  }

  public static <T> GenericResponseDTO<T> badRequest(String message) {
    return new GenericResponseDTO<>(false, message, 400, null, LocalDateTime.now(), getCurrentPath());
  }

  private static String getCurrentPath() {
    try {
      RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
      HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
      return request.getRequestURI();
    } catch (Exception e) {
      return "unknown";
    }
  }
}