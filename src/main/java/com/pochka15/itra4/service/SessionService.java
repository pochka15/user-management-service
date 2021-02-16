package com.pochka15.itra4.service;

import com.pochka15.itra4.domain.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class SessionService {
    public static void closeRequestSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        SecurityContextHolder.clearContext();
        if (session != null) session.invalidate();
    }
}
