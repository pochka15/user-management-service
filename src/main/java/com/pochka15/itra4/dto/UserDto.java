package com.pochka15.itra4.dto;

import com.pochka15.itra4.domain.user.UserActivity;
import lombok.Data;

// Эта структура данных чиста нужна для конкретного другого куска кода в контроллере.
// нужно ли писать тут какие-то пояснения?
@Data
public class UserDto {
    private final boolean enabled;
    private final UserActivity activity;
    private String name;
    private Long id;
    private String email;

    public UserDto(String name, Long id, String email, boolean enabled, UserActivity activity) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.enabled = enabled;
        this.activity = activity;
    }
}
