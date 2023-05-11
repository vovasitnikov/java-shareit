package ru.practicum.shareit.user.controller;

import ru.practicum.shareit.user.service.UserService;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import lombok.RequiredArgsConstructor;



/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    @PostMapping()
    public UserDto create(@RequestBody UserDto userDto) {
        //return userService.save(userDto);
        return null;
    }

}

