package com.bala.restaurant.controller;

import com.bala.restaurant.model.RestaurantOrder;
import com.bala.restaurant.model.RestaurantTable;
import com.bala.restaurant.model.TableStatus;
import com.bala.restaurant.service.OrderService;
import com.bala.restaurant.service.TableService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final OrderService orderService;
    private final TableService tableService;

    public HomeController(OrderService orderService, TableService tableService) {
        this.orderService = orderService;
        this.tableService = tableService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        List<RestaurantOrder> active = orderService.findActive();
        List<RestaurantTable> tables = tableService.findAll();
        model.addAttribute("todayRevenue", orderService.revenueToday());
        model.addAttribute("activeOrders", active.size());
        model.addAttribute("freeTables", tables.stream().filter(t -> t.getStatus() == TableStatus.FREE).count());
        model.addAttribute("totalTables", tables.size());
        model.addAttribute("topItems", orderService.topItemsToday());
        model.addAttribute("recentOrders", orderService.recentOrders());
        return "index";
    }
}
