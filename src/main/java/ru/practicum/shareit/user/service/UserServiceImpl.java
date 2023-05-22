package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;



@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {


    @Override
    public UserDto save(UserDto userDto) {
        validate(userDto);

        return null;
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        return null;
    }

    @Override
    public UserDto get(Long userId) {
        return null;
    }

    @Override
    public void delete(Long userId) {

    }

    @Override
    public List<UserDto> getAll() {
        return null;
    }


    private void validate(UserDto userDto) {
        if (userDto.getEmail() == null)
            throw new ValidationException("Email cannot be empty.");
        if (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@"))
            throw new ValidationException("Incorrect email: " + userDto.getEmail() + ".");
    }

}

