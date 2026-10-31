package com.bala.restaurant.service;

import com.bala.restaurant.model.RestaurantTable;
import com.bala.restaurant.model.TableStatus;
import com.bala.restaurant.repository.RestaurantOrderRepository;
import com.bala.restaurant.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TableService {

    private final RestaurantTableRepository tableRepository;
    private final RestaurantOrderRepository orderRepository;

    public TableService(RestaurantTableRepository tableRepository, RestaurantOrderRepository orderRepository) {
        this.tableRepository = tableRepository;
        this.orderRepository = orderRepository;
    }

    public List<RestaurantTable> findAll() {
        return tableRepository.findAllByOrderByTableNumber();
    }

    public long count() {
        return tableRepository.count();
    }

    public long countByStatus(TableStatus status) {
        return tableRepository.countByStatus(status);
    }

    public RestaurantTable findById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Table not found: " + id));
    }

    @Transactional
    public RestaurantTable save(RestaurantTable table) {
        if (table.getTableNumber() == null) {
            throw new IllegalStateException("Table number is required");
        }
        if (table.getCapacity() < 1 || table.getCapacity() > 30) {
            throw new IllegalStateException("Capacity must be between 1 and 30");
        }
        if (table.getStatus() == null) {
            throw new IllegalStateException("Status is required");
        }
        Long id = table.getId() == null ? -1L : table.getId();
        if (tableRepository.existsByTableNumberAndIdNot(table.getTableNumber(), id)) {
            throw new IllegalStateException("Table number already exists");
        }
        return tableRepository.save(table);
    }

    @Transactional
    public void changeStatus(Long id, TableStatus status) {
        if (status == null) {
            throw new IllegalStateException("Status is required");
        }
        RestaurantTable table = findById(id);
        table.setStatus(status);
        tableRepository.save(table);
    }

    @Transactional
    public void delete(Long id) {
        if (orderRepository.existsByTableId(id)) {
            throw new IllegalStateException("Table has order history and cannot be deleted");
        }
        tableRepository.deleteById(id);
    }
}
