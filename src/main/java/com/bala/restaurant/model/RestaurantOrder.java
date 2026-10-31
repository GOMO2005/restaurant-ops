package com.bala.restaurant.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant_orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RestaurantOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "table_id")
    private RestaurantTable table;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "taken_by")
    private User takenBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<OrderItem> items = new ArrayList<>();

    public BigDecimal getTotal() {
        return items.stream().map(OrderItem::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addItem(MenuItem menuItem, int quantity) {
        for (OrderItem existing : items) {
            if (existing.getMenuItem().getId().equals(menuItem.getId())) {
                existing.setQuantity(existing.getQuantity() + quantity);
                return;
            }
        }
        items.add(OrderItem.builder()
                .order(this)
                .menuItem(menuItem)
                .itemName(menuItem.getName())
                .unitPrice(menuItem.getPrice())
                .quantity(quantity)
                .build());
    }
}