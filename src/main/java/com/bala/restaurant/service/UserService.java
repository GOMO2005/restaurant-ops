package com.bala.restaurant.service;

import com.bala.restaurant.model.Role;
import com.bala.restaurant.model.User;
import com.bala.restaurant.repository.RestaurantOrderRepository;
import com.bala.restaurant.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RestaurantOrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RestaurantOrderRepository orderRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAllByOrderByUsername();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Transactional
    public void save(Long id, String username, String fullName, Role role, boolean enabled, String rawPassword) {
        if (role == null) {
            throw new IllegalStateException("Role is required");
        }
        if (id == null) {
            if (username == null || username.isBlank()) {
                throw new IllegalStateException("Username is required");
            }
            if (rawPassword == null || rawPassword.isBlank()) {
                throw new IllegalStateException("Password is required");
            }
            if (userRepository.existsByUsername(username.trim())) {
                throw new IllegalStateException("Username already exists");
            }
            userRepository.save(User.builder()
                    .username(username.trim())
                    .password(passwordEncoder.encode(rawPassword))
                    .fullName(fullName == null ? "" : fullName.trim())
                    .role(role)
                    .enabled(enabled)
                    .build());
        } else {
            User user = findById(id);
            user.setFullName(fullName == null ? "" : fullName.trim());
            user.setRole(role);
            user.setEnabled(enabled);
            if (rawPassword != null && !rawPassword.isBlank()) {
                user.setPassword(passwordEncoder.encode(rawPassword));
            }
            userRepository.save(user);
        }
    }

    @Transactional
    public void toggleEnabled(Long id, String currentUsername) {
        User user = findById(id);
        if (user.getUsername().equals(currentUsername)) {
            throw new IllegalStateException("You cannot disable your own account");
        }
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Transactional
    public void delete(Long id, String currentUsername) {
        User user = findById(id);
        if (user.getUsername().equals(currentUsername)) {
            throw new IllegalStateException("You cannot delete your own account");
        }
        if (orderRepository.existsByTakenById(id)) {
            throw new IllegalStateException("User has orders in history, disable the account instead");
        }
        userRepository.deleteById(id);
    }
}
