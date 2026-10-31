package com.bala.restaurant.repository;

import com.bala.restaurant.model.OrderStatus;
import com.bala.restaurant.model.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {

    List<RestaurantOrder> findByStatusInOrderByCreatedAtDesc(Collection<OrderStatus> statuses);

    @Query("select o from RestaurantOrder o left join fetch o.table left join fetch o.takenBy left join fetch o.items where o.status in :statuses order by o.createdAt desc")
    List<RestaurantOrder> findWithDetailsByStatusIn(@Param("statuses") Collection<OrderStatus> statuses);

    @Query("select o from RestaurantOrder o left join fetch o.table left join fetch o.takenBy left join fetch o.items where o.id = :id")
    Optional<RestaurantOrder> findWithDetailsById(@Param("id") Long id);

    @Query("select o from RestaurantOrder o left join fetch o.table left join fetch o.takenBy left join fetch o.items where o.id in :ids")
    List<RestaurantOrder> findWithDetailsByIdIn(@Param("ids") Collection<Long> ids);

    @Query("select coalesce(sum(oi.unitPrice * oi.quantity), 0) from RestaurantOrder o join o.items oi where o.status = :status and o.createdAt >= :start and o.createdAt < :end")
    BigDecimal revenueBetween(@Param("status") OrderStatus status, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select oi.itemName, sum(oi.quantity) from RestaurantOrder o join o.items oi where o.status = :status and o.createdAt >= :start and o.createdAt < :end group by oi.itemName order by sum(oi.quantity) desc")
    List<Object[]> topItemsBetween(@Param("status") OrderStatus status, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<RestaurantOrder> findTop5ByOrderByCreatedAtDesc();

    List<RestaurantOrder> findTop50ByStatusInOrderByCreatedAtDesc(Collection<OrderStatus> statuses);

    boolean existsByTableIdAndStatusIn(Long tableId, Collection<OrderStatus> statuses);

    boolean existsByTableId(Long tableId);

    boolean existsByTakenById(Long userId);
}
