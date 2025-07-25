package com.personal.delivery_allocation_engine.controller;

import com.personal.delivery_allocation_engine.dto.request.OrderCreateRequest;
import com.personal.delivery_allocation_engine.dto.response.OrderResponse;
import com.personal.delivery_allocation_engine.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * @author Soumyajit Podder created on 23/07/25
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  @Operation(summary = "Create a new order", description = "Creates a new food delivery order and assigns the best available partner")
  public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
    OrderResponse response = orderService.createOrder(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get order by ID", description = "Retrieves a specific order by its ID")
  public ResponseEntity<OrderResponse> getOrder(@Parameter(description = "Order ID") @PathVariable Long id) {
    OrderResponse order = orderService.getOrder(id);
    return ResponseEntity.ok(order);
  }

  @GetMapping("/partner/{partnerId}")
  @Operation(summary = "Get partner's assigned orders", description = "Retrieves all orders assigned to a specific partner")
  public ResponseEntity<List<OrderResponse>> getPartnerOrders(
      @Parameter(description = "Partner ID") @PathVariable Long partnerId) {
    List<OrderResponse> orders = orderService.getPartnerOrders(partnerId);
    return ResponseEntity.ok(orders);
  }

  @GetMapping("/user/{userId}")
  @Operation(summary = "Get user's orders", description = "Retrieves all orders for a specific user")
  public ResponseEntity<List<OrderResponse>> getUserOrders(
      @Parameter(description = "User ID") @PathVariable Long userId) {
    List<OrderResponse> orders = orderService.getUserOrders(userId);
    return ResponseEntity.ok(orders);
  }
}