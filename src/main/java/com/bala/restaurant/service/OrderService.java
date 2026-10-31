package com.bala.restaurant.service;

import com.bala.restaurant.model.MenuItem;
import com.bala.restaurant.model.OrderStatus;
import com.bala.restaurant.model.RestaurantOrder;
import com.bala.restaurant.model.RestaurantTable;
import com.bala.restaurant.model.TableStatus;
import com.bala.restaurant.model.User;
import com.bala.restaurant.repository.RestaurantOrderRepository;
import com.bala.restaurant.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final List<OrderStatus> ACTIVE =
            List.of(OrderStatus.PLACED, OrderStatus.PREPARING, OrderStatus.READY, OrderStatus.SERVED);

    private final RestaurantOrderRepository orderRepository;
    private final TableService tableService;
    private final MenuService menuService;
    private final UserRepository userRepository;

    public OrderService(RestaurantOrderRepository orderRepository, TableService tableService,
                        MenuService menuService, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.tableService = tableService;
        this.menuService = menuService;
        this.userRepository = userRepository;
    }

    public List<RestaurantOrder> findActive() {
        return orderRepository.findWithDetailsByStatusIn(ACTIVE);
    }

    public RestaurantOrder findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    @Transactional(readOnly = true)
    public RestaurantOrder findDetailById(Long id) {
        return orderRepository.findWithDetailsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    @Transactional(readOnly = true)
    public BigDecimal revenueToday() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        return orderRepository.revenueBetween(OrderStatus.PAID, start, start.plusDays(1));
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> topItemsToday() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        List<Object[]> rows = orderRepository.topItemsBetween(OrderStatus.PAID, start, start.plusDays(1));
        Map<String, Integer> top = new LinkedHashMap<>();
        for (Object[] row : rows) {
            top.put((String) row[0], ((Number) row[1]).intValue());
            if (top.size() == 5) break;
        }
        return top;
    }

    @Transactional(readOnly = true)
    public List<RestaurantOrder> recentOrders() {
        return withDetails(orderRepository.findTop5ByOrderByCreatedAtDesc());
    }

    @Transactional(readOnly = true)
    public List<RestaurantOrder> history() {
        return withDetails(orderRepository.findTop50ByStatusInOrderByCreatedAtDesc(
                List.of(OrderStatus.PAID, OrderStatus.CANCELLED)));
    }

    private List<RestaurantOrder> withDetails(List<RestaurantOrder> stubs) {
        if (stubs.isEmpty()) {
            return stubs;
        }
        List<Long> ids = stubs.stream().map(RestaurantOrder::getId).toList();
        Map<Long, RestaurantOrder> byId = orderRepository.findWithDetailsByIdIn(ids).stream()
                .collect(Collectors.toMap(RestaurantOrder::getId, o -> o));
        return stubs.stream().map(o -> byId.get(o.getId())).toList();
    }

    @Transactional
    public RestaurantOrder create(Long tableId, String username) {
        RestaurantTable table = tableService.findById(tableId);
        if (table.getStatus() == TableStatus.OUT_OF_SERVICE) {
            throw new IllegalStateException("Table is out of service");
        }
        if (orderRepository.existsByTableIdAndStatusIn(table.getId(), ACTIVE)) {
            throw new IllegalStateException("Table already has an active order");
        }
        User waiter = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
        RestaurantOrder order = RestaurantOrder.builder()
                .table(table)
                .takenBy(waiter)
                .status(OrderStatus.PLACED)
                .build();
        RestaurantOrder saved = orderRepository.save(order);
        tableService.changeStatus(table.getId(), TableStatus.OCCUPIED);
        return saved;
    }

    @Transactional
    public void addItem(Long orderId, Long menuItemId, int quantity) {
        if (quantity < 1) {
            throw new IllegalStateException("Quantity must be at least 1");
        }
        RestaurantOrder order = findById(orderId);
        if (order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalStateException("Items can only be added while order is PLACED");
        }
        MenuItem menuItem = menuService.findById(menuItemId);
        if (!menuItem.isAvailable()) {
            throw new IllegalStateException("Menu item is not available");
        }
        order.addItem(menuItem, quantity);
        orderRepository.save(order);
    }

    @Transactional
    public void removeItem(Long orderId, Long itemId) {
        RestaurantOrder order = findById(orderId);
        if (order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalStateException("Items can only be removed while order is PLACED");
        }
        boolean removed = order.getItems().removeIf(item -> item.getId().equals(itemId));
        if (!removed) {
            throw new IllegalArgumentException("Item " + itemId + " is not part of order " + orderId);
        }
        orderRepository.save(order);
    }

    @Transactional
    public RestaurantOrder transition(Long orderId, OrderStatus target) {
        RestaurantOrder order = findById(orderId);
        OrderStatus current = order.getStatus();
        boolean allowed = switch (target) {
            case PREPARING -> current == OrderStatus.PLACED;
            case READY -> current == OrderStatus.PREPARING;
            case SERVED -> current == OrderStatus.READY;
            case PAID -> current == OrderStatus.SERVED;
            case CANCELLED -> current == OrderStatus.PLACED || current == OrderStatus.PREPARING;
            default -> false;
        };
        if (!allowed) {
            throw new IllegalStateException("Cannot move order from " + current + " to " + target);
        }
        order.setStatus(target);
        if (target == OrderStatus.PAID || target == OrderStatus.CANCELLED) {
            tableService.changeStatus(order.getTable().getId(), TableStatus.FREE);
        }
        return orderRepository.save(order);
    }
}
