package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItems(Long userId, Integer from, Integer size);

    ItemDto get(Long userId, Long itemId);

    List<ItemDto> search(String text, Long userId, Integer from, Integer size);

    ItemDto save(ItemDto itemDto, ItemRequestDto itemRequestDto, Long userId);

    List<ItemDto> getItemsByRequests(List<ItemRequest> requests);

    List<ItemDto> getItemsByRequestId(Long requestId);

    ItemDto update(ItemDto itemDto, Long userId);

    void delete(Long itemId);
}