package com.personal.delivery_allocation_engine.model;

import lombok.*;
import javax.persistence.*;
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
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private double customerLat;
    private double customerLng;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partner assignedPartner;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private int cookTimeAtOrder; // in minutes
} 