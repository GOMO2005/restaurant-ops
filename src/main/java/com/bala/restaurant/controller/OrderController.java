package com.bala.restaurant.controller;

import com.bala.restaurant.model.OrderStatus;
import com.bala.restaurant.model.RestaurantOrder;
import com.bala.restaurant.model.RestaurantTable;
import com.bala.restaurant.model.TableStatus;
import com.bala.restaurant.service.MenuService;
import com.bala.restaurant.service.OrderService;
import com.bala.restaurant.service.TableService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final TableService tableService;
    private final MenuService menuService;

    public OrderController(OrderService orderService, TableService tableService, MenuService menuService) {
        this.orderService = orderService;
        this.tableService = tableService;
        this.menuService = menuService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderService.findActive());
        return "orders/list";
    }

    @GetMapping("/new")
    public String newOrder(Model model) {
        List<RestaurantTable> free = tableService.findAll().stream()
                .filter(t -> t.getStatus() == TableStatus.FREE)
                .toList();
        model.addAttribute("tables", free);
        return "orders/new";
    }

    @PostMapping
    public String create(@RequestParam Long tableId, Principal principal) {
        RestaurantOrder order = orderService.create(tableId, principal.getName());
        return "redirect:/orders/" + order.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.findDetailById(id));
        model.addAttribute("menuItems", menuService.findAll());
        return "orders/detail";
    }

    @PostMapping("/{id}/items")
    public String addItem(@PathVariable Long id, @RequestParam Long menuItemId,
                          @RequestParam(defaultValue = "1") int quantity) {
        orderService.addItem(id, menuItemId, quantity);
        return "redirect:/orders/" + id;
    }

    @PostMapping("/{orderId}/items/{itemId}/remove")
    public String removeItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        orderService.removeItem(orderId, itemId);
        return "redirect:/orders/" + orderId;
    }

    @PostMapping("/{id}/send")
    public String send(@PathVariable Long id) {
        orderService.transition(id, OrderStatus.PREPARING);
        return "redirect:/orders/" + id;
    }

    @PostMapping("/{id}/ready")
    public String ready(@PathVariable Long id) {
        orderService.transition(id, OrderStatus.READY);
        return "redirect:/orders/" + id;
    }

    @PostMapping("/{id}/serve")
    public String serve(@PathVariable Long id) {
        orderService.transition(id, OrderStatus.SERVED);
        return "redirect:/orders/" + id;
    }

    @PostMapping("/{id}/pay")
    public String pay(@PathVariable Long id) {
        orderService.transition(id, OrderStatus.PAID);
        return "redirect:/orders/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id) {
        orderService.transition(id, OrderStatus.CANCELLED);
        return "redirect:/orders/" + id;
    }

    @GetMapping("/history")
    public String history(Model model) {
        List<RestaurantOrder> closed = orderService.history();
        BigDecimal revenue = closed.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID)
                .map(RestaurantOrder::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("orders", closed);
        model.addAttribute("revenue", revenue);
        return "orders/history";
    }
}
