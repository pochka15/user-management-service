package com.pochka15.itra4.domain.security;

import com.pochka15.itra4.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handler that is used after an authentication succeeds
 */
@Component
public class CustomAuthenticationSuccessHandler extends
        SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;

    public CustomAuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    /**
     * Refresh last login date on success.
     *
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        userService.refreshLastLoginDate(authentication.getName());
        super.setAlwaysUseDefaultTargetUrl(true);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}