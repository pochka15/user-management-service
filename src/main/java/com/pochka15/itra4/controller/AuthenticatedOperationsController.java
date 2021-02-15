package com.pochka15.itra4.controller;

import com.pochka15.itra4.converter.UserToUserDtoConverter;
import com.pochka15.itra4.domain.user.User;
import com.pochka15.itra4.dto.UserDto;
import com.pochka15.itra4.form.ChosenUsersForm;
import com.pochka15.itra4.service.SecurityService;
import com.pochka15.itra4.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.pochka15.itra4.service.SecurityService.closeRequestSession;

@Controller
public class AuthenticatedOperationsController {
    private final UserService userService;
    private final UserToUserDtoConverter userToUserDtoConverter;
    private final SecurityService securityService;

    public AuthenticatedOperationsController(UserService UserService,
                                             UserToUserDtoConverter userToUserDtoConverter,
                                             SecurityService securityService) {
        this.userService = UserService;
        this.userToUserDtoConverter = userToUserDtoConverter;
        this.securityService = securityService;
    }

    @GetMapping(path = "/")
    public String home(Model model) {
        final Collection<UserDto> fetchedUsers = usersToDtos(userService.fetchAllUsers());
        model.addAttribute("users", fetchedUsers);
        model.addAttribute("formatter", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        model.addAttribute("chosenUsersForm", new ChosenUsersForm());
        return "home";
    }

    private List<UserDto> usersToDtos(Collection<? extends User> users) {
        return users.stream().map(userToUserDtoConverter::convert).collect(Collectors.toList());
    }


    @PostMapping(path = "/edit", params = "action=delete")
    public String deleteUsers(@ModelAttribute ChosenUsersForm form, Principal principal, HttpServletRequest request) {
        securityService.passAccessControl(principal.getName(),
                                          () -> userService.deleteAll(form.getChosenUserIds()),
                                          errorMessage -> closeRequestSession(request));
        return "redirect:/";
    }

    @PostMapping(path = "/edit", params = "action=block")
    public String blockUsers(@ModelAttribute ChosenUsersForm form, Principal principal, HttpServletRequest request) {
        securityService.passAccessControl(principal.getName(),
                                          () -> userService.blockUsersByIds(form.getChosenUserIds()),
                                          errorMessage -> closeRequestSession(request));
        return "redirect:/";
    }

    @PostMapping(path = "/edit", params = "action=unblock")
    public String unblockUsers(@ModelAttribute ChosenUsersForm form, Principal principal, HttpServletRequest request) {
        securityService.passAccessControl(principal.getName(),
                                          () -> userService.unblockUsersByIds(form.getChosenUserIds()),
                                          errorMessage -> closeRequestSession(request));
        return "redirect:/";
    }
}

