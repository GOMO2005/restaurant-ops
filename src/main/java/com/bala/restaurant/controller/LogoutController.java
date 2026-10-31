package com.bala.restaurant.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import com.bala.restaurant.config.FlashMessageAdvice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LogoutController {

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            Model model, Authentication authentication) {
        boolean authed = authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        if (authed) {
            return "redirect:/";
        }
        if (error != null) { model.addAttribute("loginError", true); }
        return "login";
    }

    @GetMapping("/login-debug")
    @ResponseBody
    public String loginDebug(HttpServletRequest request) {
        return "queryString=[" + request.getQueryString() + "] logout=["
                + request.getParameter("logout") + "] error=[" + request.getParameter("error") + "]";
    }

    @GetMapping("/logout")
    public String logoutGet(HttpServletRequest request, HttpServletResponse response) {
        return doLogout(request, response);
    }

    @PostMapping("/logout")
    public String logoutPost(HttpServletRequest request, HttpServletResponse response) {
        return doLogout(request, response);
    }

    private String doLogout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        request.getSession(true).setAttribute(FlashMessageAdvice.LOGOUT_KEY, "You have been signed out.");
        return "redirect:/login";
    }
}
