package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addRequest(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getMyRequests(Integer userId);

    ItemRequestDto getRequestById(Integer userId, Integer requestId);

    List<ItemRequestDto> getAllRequests(Integer userId, int from, int size);
}