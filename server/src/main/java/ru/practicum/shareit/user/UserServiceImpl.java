package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User newUser = userRepository.save(user);
        log.info("Создан пользователь id={}", newUser.getId());

        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer userId) {
        User existedUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден");
                    return new NotFoundException("Такой пользователь не найден");
                });
        if (userDto.getName() != null) {
            existedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existedUser.setEmail(userDto.getEmail());
        }
        User updUser = userRepository.save(existedUser);
        log.info("Обновлен пользователь id={}", userDto.getId());

        return UserMapper.toUserDto(updUser);
    }

    @Override
    public User getUserById(Integer userId) {
        log.info("Вызван пользователь id={}", userId);

        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь не найден");
            return new NotFoundException("Такой пользователь не найден");
        });
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Вызван список всех пользователей");

        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
        log.info("Удален пользователь id={}", userId);
    }

}