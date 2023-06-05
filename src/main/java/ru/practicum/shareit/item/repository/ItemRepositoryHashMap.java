package ru.practicum.shareit.item.repository;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.EmailException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;

@Repository
@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class ItemRepositoryHashMap {

    private static HashMap<Long, Item> itemsList = new HashMap<>();
    private static long counter;

    public Item save(Item item) throws EmailException {
        long id;
        id = itemsList.keySet().stream().max(Long::compareTo).orElse(1L);
        if (itemsList.size() == 0) {
            item.setId(id);
            counter++;
        } else {
            counter++;
            if (counter > id) {
                item.setId(counter);
            }
        }
        itemsList.put(item.getId(), item);
        return item;
    }

    public Item update(Item item) {
        itemsList.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return itemsList.get(id);
    }

    public void deleteById(Long id) {
        itemsList.remove(id);
    }

}
