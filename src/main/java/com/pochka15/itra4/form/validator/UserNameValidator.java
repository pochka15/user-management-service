package com.pochka15.itra4.form.validator;

import com.pochka15.itra4.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserNameValidator implements ConstraintValidator<UsernameIsFree, String> {
    private final UserService userService;

    public UserNameValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return userService.findByName(username).isEmpty();
    }
}
