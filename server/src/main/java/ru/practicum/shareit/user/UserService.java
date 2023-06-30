package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Integer userId);

    User getUserById(Integer userId);

    List<UserDto> getAllUsers();

    void deleteUser(Integer userId);
}