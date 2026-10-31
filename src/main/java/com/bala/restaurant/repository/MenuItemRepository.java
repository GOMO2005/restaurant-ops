package com.bala.restaurant.repository;

import com.bala.restaurant.model.MenuCategory;
import com.bala.restaurant.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findAllByOrderByName();
    List<MenuItem> findByCategoryOrderByName(MenuCategory category);
}