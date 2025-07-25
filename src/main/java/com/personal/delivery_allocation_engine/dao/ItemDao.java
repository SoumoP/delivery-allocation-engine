package com.personal.delivery_allocation_engine.dao;

import com.personal.delivery_allocation_engine.entity.Item;
import com.personal.delivery_allocation_engine.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * @author Soumyajit Podder created on 25/07/25
 */
@Service
@RequiredArgsConstructor
public class ItemDao {
  private final ItemRepository itemRepository;

  public List<Item> findAllByIdIn(List<Long> itemIds) {
    if (CollectionUtils.isEmpty(itemIds)) {
      return List.of();
    }
    return itemRepository.findAllByIdIn(itemIds);
  }
}
