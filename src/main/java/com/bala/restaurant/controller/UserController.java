package com.bala.restaurant.controller;

import com.bala.restaurant.model.Role;
import com.bala.restaurant.model.User;
import com.bala.restaurant.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "users/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("roles", Role.values());
        return "users/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam(required = false) String username,
                       @RequestParam(required = false) String fullName,
                       @RequestParam(required = false) Role role,
                       @RequestParam(defaultValue = "false") boolean enabled,
                       @RequestParam(required = false) String rawPassword) {
        userService.save(id, username, fullName, role, enabled, rawPassword);
        return "redirect:/users";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, Principal principal) {
        userService.toggleEnabled(id, principal.getName());
        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        userService.delete(id, principal.getName());
        return "redirect:/users";
    }
}
