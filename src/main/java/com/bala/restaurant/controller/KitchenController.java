package com.bala.restaurant.controller;

import com.bala.restaurant.model.OrderStatus;
import com.bala.restaurant.model.RestaurantOrder;
import com.bala.restaurant.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/kitchen")
public class KitchenController {

    private final OrderService orderService;

    public KitchenController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String board(Model model) {
        List<RestaurantOrder> orders = orderService.findActive();
        model.addAttribute("newOrders",
                orders.stream().filter(o -> o.getStatus() == OrderStatus.PLACED).toList());
        model.addAttribute("cookingOrders",
                orders.stream().filter(o -> o.getStatus() == OrderStatus.PREPARING).toList());
        model.addAttribute("readyOrders",
                orders.stream().filter(o -> o.getStatus() == OrderStatus.READY).toList());
        return "kitchen/board";
    }

    @PostMapping("/{id}/start")
    public String start(@PathVariable Long id) {
        orderService.transition(id, OrderStatus.PREPARING);
        return "redirect:/kitchen";
    }

    @PostMapping("/{id}/ready")
    public String ready(@PathVariable Long id) {
        orderService.transition(id, OrderStatus.READY);
        return "redirect:/kitchen";
    }
}
