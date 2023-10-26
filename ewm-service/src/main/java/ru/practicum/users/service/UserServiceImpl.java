package ru.practicum.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User create(User user) {
        validateUserNameAndEmail(user);
        userRepository.save(user);
        log.info("Added user with id: {}", user.getId());
        return user;
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id=" + userId + " was not found"));
        userRepository.deleteById(userId);
        log.info("Deleted user with id: {}", userId);
    }

    @Override
    public List<UserDto> findAll(List<Long> ids, Integer from, Integer size) {
        log.info("Get users with ids: {}", ids);
        Pageable pageable = PageRequest.of(from / size, size);
        List<User> users = userRepository.findAllByIds(ids, pageable);
        return users.stream().map(UserMapper::getUserDto).collect(Collectors.toList());
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
