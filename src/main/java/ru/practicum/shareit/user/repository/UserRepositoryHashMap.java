package ru.practicum.shareit.user.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Repository
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRepositoryHashMap {
    private HashMap<Long, UserDto> usersList = new HashMap<>();

    public UserDto save(UserDto userDto) {

        //наибольший айдишник
        Map.Entry<Long, UserDto> maxId = null;
        for (Map.Entry<Long, UserDto> entry : usersList.entrySet()) {
            if (maxId == null || (entry.getKey() > maxId.getKey())) {
                maxId = entry;
            }
        }
        Long generateId = maxId.getKey() + 1;

        usersList.put(generateId, userDto);
        return userDto;
    }
}
