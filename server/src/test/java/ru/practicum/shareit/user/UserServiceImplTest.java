package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock                                            // создание мока для тестового класса
    private UserRepository userRepository;
    @InjectMocks                                     // поле для внедрения моков (объект тестируемого класса)
    private UserServiceImpl userService;

    @Test
    void createUser_returnSavedUser() {
        User user = new User(1, "name", "e@mail.ya");
        when(userRepository.save(user)).thenReturn(user);
        UserDto userDto = UserMapper.toUserDto(user);

        UserDto newUserDto = userService.createUser(userDto);

        assertEquals(userDto, newUserDto);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_returnUpdatedUser() {
        int userId = 1;
        User user = new User(userId, "name", "e@mail.ya");
        User updUser = new User(userId, "newname", "new@mail.ya");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(updUser);
        UserDto userDto = UserMapper.toUserDto(updUser);

        UserDto newUserDto = userService.updateUser(userDto, userId);

        assertEquals(userDto, newUserDto);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_wrongUser() {
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(new UserDto(), userId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_returnUser() {
        int userId = 0;
        User user = new User();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        User gotUser = userService.getUserById(userId);

        assertEquals(user, gotUser);
    }

    @Test
    void getUserById_wrongId() {
        int userId = 0;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(userId));
        assertEquals("Такой пользователь не найден", nonUser.getMessage());
    }

    @Test
    void getAllUsers_returnListSize() {
        List<UserDto> userList = List.of(new UserDto());
        when(userRepository.findAll())
                .thenReturn(userList
                        .stream()
                        .map(UserMapper::toUser)
                        .collect(Collectors.toList()));

        List<UserDto> responseList = userService.getAllUsers();

        assertEquals(1, responseList.size());
        assertEquals(userList, responseList);
    }

    @Test
    void getAllUsers_emptyList() {
        when(userRepository.findAll())
                .thenReturn(new ArrayList<>());

        List<UserDto> responseList = userService.getAllUsers();

        assertEquals(0, responseList.size());
        assertEquals(new ArrayList<>(), responseList);
    }

    @Test
    void deleteUser() {
        int userId = 1;
        userService.deleteUser(userId);

        verify(userRepository, atMostOnce()).deleteById(userId);
    }

}