package com.bala.restaurant.config;

import com.bala.restaurant.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/h2-console/**", "/css/**", "/js/**", "/images/**", "/error").permitAll()
                .requestMatchers("/users/**").hasRole("ADMIN")
                .requestMatchers("/tables/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/kitchen/**").hasAnyRole("KITCHEN", "ADMIN", "MANAGER")
                .requestMatchers("/orders/*/ready").hasAnyRole("KITCHEN", "ADMIN", "MANAGER")
                .requestMatchers("/orders/*/cancel").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/menu/new", "/menu/save", "/menu/*/edit", "/menu/*/toggle", "/menu/*/delete")
                    .hasAnyRole("ADMIN", "MANAGER")
                .anyRequest().authenticated())
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .formLogin(form -> form.loginPage("/login").permitAll())
            .logout(AbstractHttpConfigurer::disable)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new SessionExpiredEntryPoint())
                .accessDeniedHandler(new FriendlyAccessDeniedHandler()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
