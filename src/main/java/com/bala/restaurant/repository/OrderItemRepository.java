package com.bala.restaurant.repository;

import com.bala.restaurant.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsByMenuItemId(Long menuItemId);
}
