package com.pochka15.itra4.converter;

import com.pochka15.itra4.domain.user.Role;
import com.pochka15.itra4.domain.user.User;
import com.pochka15.itra4.form.CredentialsForm;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CredentialsToUserConverter implements Converter<CredentialsForm, User> {
    @Override
    @NonNull
    public User convert(CredentialsForm source) {
        return User.builder()
                .name(source.getName())
                .password(source.getPassword())
                .email(source.getEmail())
                .roles(Collections.singleton(Role.USER))
                .build();
    }
}
