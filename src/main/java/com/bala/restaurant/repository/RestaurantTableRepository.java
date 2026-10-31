package com.bala.restaurant.repository;

import com.bala.restaurant.model.RestaurantTable;
import com.bala.restaurant.model.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    List<RestaurantTable> findAllByOrderByTableNumber();

    boolean existsByTableNumberAndIdNot(Integer tableNumber, Long id);

    long countByStatus(TableStatus status);
}
