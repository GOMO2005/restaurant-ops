package com.bala.restaurant.service;

import com.bala.restaurant.model.MenuCategory;
import com.bala.restaurant.model.MenuItem;
import com.bala.restaurant.repository.MenuItemRepository;
import com.bala.restaurant.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final OrderItemRepository orderItemRepository;

    public MenuService(MenuItemRepository menuItemRepository, OrderItemRepository orderItemRepository) {
        this.menuItemRepository = menuItemRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<MenuItem> findAll() {
        return menuItemRepository.findAllByOrderByName();
    }

    public List<MenuItem> findByCategory(MenuCategory category) {
        return menuItemRepository.findByCategoryOrderByName(category);
    }

    public MenuItem findById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + id));
    }

    @Transactional
    public MenuItem save(MenuItem item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new IllegalStateException("Name is required");
        }
        if (item.getPrice() == null || item.getPrice().signum() < 0) {
            throw new IllegalStateException("Price must be zero or greater");
        }
        if (item.getCategory() == null) {
            throw new IllegalStateException("Category is required");
        }
        item.setName(item.getName().trim());
        if (item.getDescription() != null && item.getDescription().isBlank()) {
            item.setDescription(null);
        }
        return menuItemRepository.save(item);
    }

    @Transactional
    public void toggleAvailability(Long id) {
        MenuItem item = findById(id);
        item.setAvailable(!item.isAvailable());
        menuItemRepository.save(item);
    }

    @Transactional
    public void delete(Long id) {
        if (orderItemRepository.existsByMenuItemId(id)) {
            throw new IllegalStateException("Menu item is used in existing orders - disable it instead of deleting");
        }
        menuItemRepository.deleteById(id);
    }
}
