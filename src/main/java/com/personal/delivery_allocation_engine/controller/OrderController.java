package com.personal.delivery_allocation_engine.controller;

import com.personal.delivery_allocation_engine.dto.OrderCreateRequest;
import com.personal.delivery_allocation_engine.dto.OrderResponse;
import com.personal.delivery_allocation_engine.model.Order;
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
@RequestMapping("/api/orders")
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

  @GetMapping
  @Operation(summary = "Get all orders", description = "Retrieves all orders in the system")
  public ResponseEntity<List<OrderResponse>> getAllOrders() {
    List<OrderResponse> orders = orderService.getAllOrders();
    return ResponseEntity.ok(orders);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get order by ID", description = "Retrieves a specific order by its ID")
  public ResponseEntity<OrderResponse> getOrder(
      @Parameter(description = "Order ID") @PathVariable Long id) {
    OrderResponse order = orderService.getOrder(id);
    return ResponseEntity.ok(order);
  }

  @PutMapping("/{id}/status")
  @Operation(summary = "Update order status", description = "Updates the status of an existing order")
  public ResponseEntity<OrderResponse> updateOrderStatus(
      @Parameter(description = "Order ID") @PathVariable Long id,
      @Parameter(description = "New order status") @RequestBody Order.OrderStatus status) {
    OrderResponse response = orderService.updateOrderStatus(id, status);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/partner/{partnerId}")
  @Operation(summary = "Get partner's assigned orders", description = "Retrieves all orders assigned to a specific partner")
  public ResponseEntity<List<OrderResponse>> getPartnerOrders(
      @Parameter(description = "Partner ID") @PathVariable Long partnerId) {
    List<OrderResponse> orders = orderService.getPartnerOrders(partnerId);
    return ResponseEntity.ok(orders);
  }
}