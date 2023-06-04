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
@NoArgsConstructor
@Slf4j
public class ItemRepositoryHashMap {

    private static HashMap<Long, Item> itemsList = new HashMap<>();

    public  Item save(Item item) throws EmailException {

        if (itemsList.size() == 0) {
            item.setId(1L);
        } else {
            item.setId(itemsList.size() + 1L);
        }
        itemsList.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id){
        return   itemsList.get(id);
    }

    public void deleteById(Long id) {
        itemsList.remove(id);
    }

}
