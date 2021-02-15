package com.pochka15.itra4.service;

import com.pochka15.itra4.domain.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class SecurityService {
    private final UserService userService;

    public SecurityService(UserService userService) {
        this.userService = userService;
    }

    public static void closeRequestSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        SecurityContextHolder.clearContext();
        if (session != null) session.invalidate();
    }

    public void passAccessControl(String userName,
                                  Runnable onSuccess,
                                  Consumer<AccessControlError> errorHandler) {
        final Optional<User> foundUser = userService.findByName(userName);
        if (foundUser.isEmpty()) errorHandler.accept(AccessControlError.USERNAME_NOT_FOUND);
        else if (!foundUser.get().isEnabled()) errorHandler.accept(AccessControlError.USER_BLOCKED);
        else onSuccess.run();
    }

    public enum AccessControlError {
        USERNAME_NOT_FOUND,
        USER_BLOCKED
    }
}
