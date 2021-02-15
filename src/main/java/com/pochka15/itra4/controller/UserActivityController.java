package com.pochka15.itra4.controller;

import com.pochka15.itra4.converter.CredentialsToUserConverter;
import com.pochka15.itra4.form.CredentialsForm;
import com.pochka15.itra4.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class UserActivityController {
    private final CredentialsToUserConverter credentialsToUserConverter;
    private final UserService userService;

    public UserActivityController(CredentialsToUserConverter credentialsToUserConverter,
                                  UserService userService) {
        this.credentialsToUserConverter = credentialsToUserConverter;
        this.userService = userService;
    }

    @GetMapping(path = "/login")
    public String login() {
        return "login";
    }

    @PostMapping(path = "/register")
    public String registerUser(@ModelAttribute("credentials") @Valid CredentialsForm credentials, BindingResult result) {
        if (result.hasErrors()) return "registration";
        userService.saveNew(credentialsToUserConverter.convert(credentials));
        return "redirect:login";
    }

    @GetMapping(path = "/register")
    public String register(Model model) {
        model.addAttribute("credentials", new CredentialsForm());
        return "registration";
    }
}
