package ru.practicum.shareit.user.service;

import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.error.EmailException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;
import ru.practicum.shareit.user.repository.UserRepositoryHashMap;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepositoryHashMap userRepositoryHashMap;

    @Override
    public UserDto save(UserDto userDto) throws EmailException, ValidationException {

        validate(userDto);
        return userRepositoryHashMap.save(userDto);
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        return userRepositoryHashMap.update(userDto, userId);
    }

    @Override
    public UserDto get(Long userId) {
        return userRepositoryHashMap.get(userId);
    }

    @Override
    public void delete(Long userId) {
        userRepositoryHashMap.delete(userId);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepositoryHashMap.getAll();
    }

    private void validate(UserDto userDto) {
        if (userDto.getEmail() == null)
            throw new ValidationException("Email cannot be empty.");
        if (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@"))
            throw new ValidationException("Incorrect email: " + userDto.getEmail() + ".");
    }
}