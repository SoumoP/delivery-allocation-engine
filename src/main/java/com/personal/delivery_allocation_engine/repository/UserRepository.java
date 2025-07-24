package com.personal.delivery_allocation_engine.repository;

import com.personal.delivery_allocation_engine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
