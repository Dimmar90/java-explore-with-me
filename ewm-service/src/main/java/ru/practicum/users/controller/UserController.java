package ru.practicum.users.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;
import ru.practicum.users.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        validateUserNameAndEmail(user);
        return userService.create(user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<UserDto> findAll(@RequestParam(required = false) List<Long> ids,
                                 @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                 @RequestParam(required = false, defaultValue = "10") @PositiveOrZero Integer size) {
        return userService.findAll(ids, from, size);
    }

    private void validateUserNameAndEmail(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new BadRequestException("Field: name. Error: must not be blank. Value: null");
        }
        if (user.getName().length() < 2 || user.getName().length() > 250) {
            throw new BadRequestException("Field: name. Error: length must not be less 2, or larger 250 symbols. Value: "
                    + user.getName().length());
        }
        if (userRepository.existsUserByName(user.getName())) {
            throw new ConflictException("Field: name. Error: user with name '" + user.getName() + "' already exist");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BadRequestException("Field: email. Error: must not be blank. Value: null");
        }
        if (user.getEmail().length() < 6 || user.getEmail().length() > 254) {
            throw new BadRequestException("Field: email. Error: length must not be less 6, or larger 254 symbols. Value: "
                    + user.getEmail().length());
        }
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new ConflictException("Field: email. Error: user with email '" + user.getEmail() + "' already exist");
        }
    }
}
