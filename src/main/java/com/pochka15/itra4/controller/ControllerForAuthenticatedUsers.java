package com.pochka15.itra4.controller;

import com.pochka15.itra4.converter.UserToUserDtoConverter;
import com.pochka15.itra4.domain.user.User;
import com.pochka15.itra4.dto.UserDto;
import com.pochka15.itra4.form.ChosenUsersForm;
import com.pochka15.itra4.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pochka15.itra4.service.SessionService.closeRequestSession;

/**
 * Controller for handling requests from authenticated users
 */
@Controller
public class ControllerForAuthenticatedUsers {
    private final UserService userService;
    private final UserToUserDtoConverter userToUserDtoConverter;

    public ControllerForAuthenticatedUsers(UserService UserService,
                                           UserToUserDtoConverter userToUserDtoConverter) {
        this.userService = UserService;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    @GetMapping(path = "/")
    public String home(Model model) {
        addHomeAttributes(model, Set.of(), Set.of());
        return "home";
    }

    @GetMapping(path = "/with-blocked")
    public String redirectWithBlockedIds(Model model, @ModelAttribute("blockedIds") Set<Long> blockedIds) {
        addHomeAttributes(model, blockedIds, Set.of());
        return "home";
    }

    @GetMapping(path = "/with-unblocked")
    public String redirectWithUnblockedIds(Model model, @ModelAttribute("unblockedIds") Set<Long> unblockedIds) {
        addHomeAttributes(model, Set.of(), unblockedIds);
        return "home";
    }

//    Привязка к Long id, не очень?!

    /**
     * All the attributes that must be added for the "home" template
     *
     * @param model        object on which attributes will be added
     * @param blockedIds   set containing ids of the users that are blocked
     * @param unblockedIds set containing ids of the users that are unblocked
     * @return updated model instance
     */
    private Model addHomeAttributes(Model model, Set<Long> blockedIds, Set<Long> unblockedIds) {
        return model.addAttribute("users", convertToDtos(userService.fetchAllUsers()))
                .addAttribute("formatter", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .addAttribute("chosenUsersForm", new ChosenUsersForm())
                .addAttribute("unblockedIds", unblockedIds)
                .addAttribute("blockedIds", blockedIds);
    }

    private List<UserDto> convertToDtos(Collection<? extends User> users) {
        return users.stream().map(userToUserDtoConverter::convert).collect(Collectors.toList());
    }

    @PostMapping(path = "/edit", params = "action=delete")
    public String deleteUsers(@ModelAttribute ChosenUsersForm form) {
        userService.deleteAll(form.getChosenUserIds());
        return "redirect:/";
    }

    @PostMapping(path = "/edit", params = "action=block")
    public RedirectView blockUsers(@ModelAttribute ChosenUsersForm form,
                                   Principal principal,
                                   HttpServletRequest request,
                                   RedirectAttributes attributes) {
        userService.blockUsersByIds(form.getChosenUserIds());
        attributes.addFlashAttribute("blockedIds", form.getChosenUserIds());
//        Close current user's session if he blocked himself
        Optional<User> foundUser = userService.findByName(principal.getName());
        foundUser.ifPresent(user -> {
            if (!user.isEnabled()) closeRequestSession(request);
        });
        return new RedirectView("/with-blocked");
    }

    @PostMapping(path = "/edit", params = "action=unblock")
    public RedirectView unblockUsers(@ModelAttribute ChosenUsersForm form,
                                     Principal principal,
                                     HttpServletRequest request,
                                     RedirectAttributes attributes) {
        userService.unblockUsersByIds(form.getChosenUserIds());
        attributes.addFlashAttribute("unblockedIds", form.getChosenUserIds());
        return new RedirectView("/with-unblocked");
    }
}

