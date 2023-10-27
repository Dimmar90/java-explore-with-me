package ru.practicum.users.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.users.model.User;

@UtilityClass
public class UserMapper {
    public static UserDto getUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto getUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
