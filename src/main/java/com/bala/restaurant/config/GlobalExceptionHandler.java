package com.bala.restaurant.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.net.URISyntaxException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public String handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        log.warn("{} on {}: {}", ex.getClass().getSimpleName(), request.getRequestURI(), ex.getMessage());
        return flash(request, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return flash(request, "Invalid value for '" + ex.getName() + "'");
    }

    @ExceptionHandler(BindException.class)
    public String handleBind(BindException ex, HttpServletRequest request) {
        String msg = ex.getBindingResult().getAllErrors().isEmpty() ? "Invalid form values"
                : ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return flash(request, msg);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        return flash(request, "Missing required parameter: " + ex.getParameterName());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation on {}: {}", request.getRequestURI(), ex.getMessage());
        return flash(request, "That record is still referenced by other data");
    }

    private String flash(HttpServletRequest request, String message) {
        HttpSession session = request.getSession(true);
        session.setAttribute(FlashMessageAdvice.ERROR_KEY, message);
        return back(request);
    }

    private String back(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                URI uri = new URI(referer);
                String path = uri.getRawPath();
                if (path == null || path.isBlank() || path.startsWith("/h2-console")
                        || path.startsWith("/login") || path.startsWith("/logout")) {
                    return "redirect:/";
                }
                return "redirect:" + path + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery());
            } catch (URISyntaxException ignored) {
                // fall through to home
            }
        }
        return "redirect:/";
    }
}
