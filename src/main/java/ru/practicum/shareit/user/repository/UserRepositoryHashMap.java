package ru.practicum.shareit.user.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.EmailException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class UserRepositoryHashMap {
    private static HashMap<Long, UserDto> usersList = new HashMap<>();

    public UserDto save(UserDto userDto) throws EmailException{
        //наибольший айдишник
        //так я генерирую айдишник
        //а где он тогда будет формироваться, если не здесь???
        //и как?
//        Map.Entry<Long, UserDto> maxId = null;
//        for (Map.Entry<Long, UserDto> entry : usersList.entrySet()) {
//            if (maxId == null || (entry.getKey() > maxId.getKey())) {
//                maxId = entry;
//            }
//        }
        if (usersList.size() == 0) {
            userDto.setId(1L);
        } else {
            userDto.setId(usersList.size() + 1L);
        }
        for (Map.Entry<Long, UserDto> values : usersList.entrySet()) {
            if (userDto.getEmail().equals(values.getValue().getEmail())) {
                throw new EmailException("User with email: " + userDto.getEmail() + " is already exist.");
            }
        }
//        Long generateId = maxId.getKey() + 1;
        usersList.put(userDto.getId(), userDto);
        return userDto;
    }

    public UserDto update(UserDto userDto, Long userId) {
        userDto.setId(userId);
        usersList.put(userDto.getId(), userDto);
        return userDto;
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
