package com.bala.restaurant.config;

import com.bala.restaurant.model.MenuCategory;
import com.bala.restaurant.model.MenuItem;
import com.bala.restaurant.model.RestaurantTable;
import com.bala.restaurant.model.Role;
import com.bala.restaurant.model.User;
import com.bala.restaurant.repository.MenuItemRepository;
import com.bala.restaurant.repository.RestaurantTableRepository;
import com.bala.restaurant.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository, MenuItemRepository menuItemRepository,
                               RestaurantTableRepository tableRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(User.builder()
                        .username("admin").password(passwordEncoder.encode("admin123"))
                        .fullName("System Administrator").role(Role.ADMIN).enabled(true).build());
                userRepository.save(User.builder()
                        .username("waiter1").password(passwordEncoder.encode("waiter123"))
                        .fullName("Ravi Kumar").role(Role.WAITER).enabled(true).build());
                userRepository.save(User.builder()
                        .username("chef1").password(passwordEncoder.encode("chef123"))
                        .fullName("Chef Anand").role(Role.KITCHEN).enabled(true).build());
                System.out.println(">>> Seeded users: admin/admin123, waiter1/waiter123, chef1/chef123");
            }
            if (menuItemRepository.count() == 0) {
                menuItemRepository.save(MenuItem.builder().name("Paneer Butter Masala")
                        .description("Creamy tomato gravy with paneer").price(new BigDecimal("220.00"))
                        .category(MenuCategory.MAIN_COURSE).build());
                menuItemRepository.save(MenuItem.builder().name("Chicken Biryani")
                        .description("Hyderabadi style, served with raita").price(new BigDecimal("280.00"))
                        .category(MenuCategory.RICE_BIRYANI).build());
                menuItemRepository.save(MenuItem.builder().name("Gobi Manchurian")
                        .description("Crispy cauliflower in tangy sauce").price(new BigDecimal("160.00"))
                        .category(MenuCategory.STARTER).build());
                menuItemRepository.save(MenuItem.builder().name("Butter Naan")
                        .description("Tandoori bread with butter").price(new BigDecimal("45.00"))
                        .category(MenuCategory.BREADS).build());
                menuItemRepository.save(MenuItem.builder().name("Gulab Jamun")
                        .description("Warm, syrup soaked, 2 pieces").price(new BigDecimal("80.00"))
                        .category(MenuCategory.DESSERTS).build());
                menuItemRepository.save(MenuItem.builder().name("Masala Chai")
                        .description("Spiced Indian tea").price(new BigDecimal("30.00"))
                        .category(MenuCategory.BEVERAGES).build());
                System.out.println(">>> Seeded 6 menu items");
            }
            if (tableRepository.count() == 0) {
                int[] capacities = {2, 4, 4, 6, 2, 4, 4, 8};
                for (int i = 0; i < capacities.length; i++) {
                    tableRepository.save(RestaurantTable.builder()
                            .tableNumber(i + 1).capacity(capacities[i]).build());
                }
                System.out.println(">>> Seeded 8 tables");
            }
        };
    }
}