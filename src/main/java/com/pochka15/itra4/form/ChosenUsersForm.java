package com.pochka15.itra4.form;

import lombok.Data;

import java.util.Set;

@Data
public class ChosenUsersForm {
    private Set<Long> chosenUserIds = Set.of();
}
