package com.bala.restaurant.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class SessionExpiredEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            HttpSession session = request.getSession(true);
            session.setAttribute(FlashMessageAdvice.LOGOUT_KEY, "Your session expired. Please sign in again.");
        }
        response.sendRedirect("/login");
    }
}
