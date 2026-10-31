package com.bala.restaurant.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.net.URI;

public class FriendlyAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        HttpSession session = request.getSession(true);
        session.setAttribute(FlashMessageAdvice.ERROR_KEY, "You do not have permission to do that");
        response.sendRedirect(safeReferer(request));
    }

    private String safeReferer(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                URI uri = new URI(referer);
                String path = uri.getRawPath();
                if (path != null && !path.isBlank() && !path.startsWith("/h2-console")
                        && !path.startsWith("/login") && !path.startsWith("/logout")) {
                    return path + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery());
                }
            } catch (Exception ignored) {
                // fall through to home
            }
        }
        return "/";
    }
}
