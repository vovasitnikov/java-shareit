package ru.practicum.shareit.item.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class ItemRepositoryHashMap {

    private static HashMap<Long, Item> itemsList = new HashMap<>();
    private static long counter = 0;

    public Item save(Item item)  {
        long id;
        id = itemsList.keySet().stream().max(Long::compareTo).orElse(1L);
        if (itemsList.size() == 0) {
            item.setId(id);
            setCounter(1);
        } else {
            setCounter((int) counter + 1);
            if (counter > id) {
                item.setId(counter);
            }
        }
        itemsList.put(item.getId(), item);
        log.info(itemsList.toString());
        return item;
    }

    public static synchronized void setCounter(int counter) {
        ItemRepositoryHashMap.counter = counter;
    }

    public Item update(Item item) {
        itemsList.put(item.getId(), item);
        log.info(itemsList.toString());
        return item;
    }

    public Item findById(Long id) {
        return itemsList.get(id);
    }

    public void deleteById(Long id) {
        itemsList.remove(id);
    }

    public List<Item> getAll() {
        List<Item> userDtoList = new ArrayList<>();
        for (Map.Entry<Long, Item> pair : itemsList.entrySet()) {
            userDtoList.add(pair.getValue());
        }
        return userDtoList;
    }
}