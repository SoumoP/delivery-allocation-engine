package com.personal.delivery_allocation_engine.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Soumyajit Podder created on 25/07/25
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String description;
  @ManyToOne
  @JoinColumn(name = "restaurant_id")
  private Restaurant restaurant;
}
