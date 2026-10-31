package com.bala.restaurant.controller;

import com.bala.restaurant.model.MenuCategory;
import com.bala.restaurant.model.MenuItem;
import com.bala.restaurant.service.MenuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) MenuCategory category, Model model) {
        model.addAttribute("categories", MenuCategory.values());
        model.addAttribute("selected", category);
        model.addAttribute("items", category == null ? menuService.findAll() : menuService.findByCategory(category));
        return "menu/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("item", new MenuItem());
        model.addAttribute("categories", MenuCategory.values());
        return "menu/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("item", menuService.findById(id));
        model.addAttribute("categories", MenuCategory.values());
        return "menu/form";
    }

    @PostMapping("/save")
    public String save(MenuItem item) {
        menuService.save(item);
        return "redirect:/menu";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        menuService.toggleAvailability(id);
        return "redirect:/menu";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        menuService.delete(id);
        return "redirect:/menu";
    }
}
