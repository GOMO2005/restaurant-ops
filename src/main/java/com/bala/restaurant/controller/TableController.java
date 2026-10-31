package com.bala.restaurant.controller;

import com.bala.restaurant.model.RestaurantTable;
import com.bala.restaurant.model.TableStatus;
import com.bala.restaurant.service.TableService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tables", tableService.findAll());
        return "tables/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("table", new RestaurantTable());
        model.addAttribute("statuses", TableStatus.values());
        return "tables/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("table", tableService.findById(id));
        model.addAttribute("statuses", TableStatus.values());
        return "tables/form";
    }

    @PostMapping("/save")
    public String save(RestaurantTable table) {
        tableService.save(table);
        return "redirect:/tables";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam String status) {
        tableService.changeStatus(id, TableStatus.valueOf(status.trim().toUpperCase()));
        return "redirect:/tables";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        tableService.delete(id);
        return "redirect:/tables";
    }
}
