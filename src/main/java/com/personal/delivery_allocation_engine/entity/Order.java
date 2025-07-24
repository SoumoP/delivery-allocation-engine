package com.personal.delivery_allocation_engine.entity;

import com.personal.delivery_allocation_engine.enums.OrderStatus;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
  @ManyToOne
  @JoinColumn(name = "partner_id")
  private Partner partner;
  @ManyToOne
  @JoinColumn(name = "restaurant_id")
  private Restaurant restaurant;
  @CreationTimestamp
  private LocalDateTime createdAt;
  @Enumerated(EnumType.STRING)
  private OrderStatus status;
}