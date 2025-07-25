package com.personal.delivery_allocation_engine.repository;

import com.personal.delivery_allocation_engine.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * @author Soumyajit Podder created on 25/07/25
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
  List<Item> findAllByIdIn(Collection<Long> ids);
}
