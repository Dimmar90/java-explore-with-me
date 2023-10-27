package ru.practicum.users.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.User;

import java.util.List;

public interface UserService {
    @Transactional
    User create(User user);

    void deleteUserById(Long userId);

    List<UserDto> findAll(List<Long> ids, Integer from, Integer size);
}
