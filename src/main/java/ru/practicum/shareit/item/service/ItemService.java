package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItems(Long userId);

    ItemDto get(Long userId, Long itemId);

    List<ItemDto> search(String text);

    ItemDto save(ItemDto itemDto,  Long userId);

    List<ItemDto> getItemsByRequestId(Long requestId);

    ItemDto update(ItemDto itemDto, Long userId);

    void delete(Long itemId);
}