package ru.practicum.shareit.user.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.EmailException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

@Repository
@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class UserRepositoryHashMap {
    private static HashMap<Long, UserDto> usersList = new HashMap<>();
    private static long counter;

    public UserDto save(UserDto userDto) throws EmailException {
        long id;
        id = usersList.keySet().stream().max(Long::compareTo).orElse(1L);
        if (usersList.size() == 0) {
            userDto.setId(id);
            counter++;
        } else {
            for (Map.Entry<Long, UserDto> values : usersList.entrySet()) {
                if (values.getValue().getEmail().equals(userDto.getEmail())) {
                    throw new EmailException("User with email: " + userDto.getEmail() + " is already exist.");
                }
            }
            counter++;
            if (counter > id) {
                userDto.setId(counter);
            }
        }
        usersList.put(userDto.getId(), userDto);
        return userDto;
    }

    public UserDto update(UserDto userDto, Long userId) {
        UserDto userDtoFromBase = usersList.get(userId);
        if (userDtoFromBase == null) throw new NotFoundException("User does not exists");
        for (Map.Entry<Long, UserDto> values : usersList.entrySet()) {
            if (values.getValue().getEmail().equals(userDto.getEmail()) && !Objects.equals(userId, values.getKey())) {
                throw new EmailException("User with email: " + userDto.getEmail() + " is already exist.");
            }
        }
        if (userDto.getEmail() != null) userDtoFromBase.setEmail(userDto.getEmail());
        if (userDto.getName() != null) userDtoFromBase.setName(userDto.getName());
        usersList.put(userId, userDtoFromBase);
        return userDtoFromBase;
    }

    public UserDto get(Long userId) {
        UserDto userDto = usersList.get(userId);
        if (userDto == null) throw new NotFoundException("User does not exists");
        return userDto;
    }

    public void delete(Long userId) {
        usersList.remove(userId);
    }

    public List<UserDto> getAll() {
        List<UserDto> userDtoList = new ArrayList<>();
        for (Map.Entry<Long, UserDto> pair : usersList.entrySet()) {
            userDtoList.add(pair.getValue());
        }
        return userDtoList;
    }
}
