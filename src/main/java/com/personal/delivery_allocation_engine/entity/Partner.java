package com.personal.delivery_allocation_engine.entity;

import com.personal.delivery_allocation_engine.enums.PartnerStatus;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Partner {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String mobile;
  private PartnerStatus status;
  private double performanceScore;
} 