package com.bala.restaurant.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class FlashMessageAdvice {

    public static final String ERROR_KEY = "FLASH_ERROR";
    public static final String LOGOUT_KEY = "FLASH_LOGOUT";

    @ModelAttribute("error")
    public String popError(HttpSession session) {
        String msg = (String) session.getAttribute(ERROR_KEY);
        if (msg != null) {
            session.removeAttribute(ERROR_KEY);
        }
        return msg;
    }

    @ModelAttribute("logoutMsg")
    public String popLogoutMsg(HttpSession session) {
        String msg = (String) session.getAttribute(LOGOUT_KEY);
        if (msg != null) {
            session.removeAttribute(LOGOUT_KEY);
        }
        return msg;
    }
}
