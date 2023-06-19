package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.mapToItemRequest;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.mapToItemRequestDto;
import static ru.practicum.shareit.user.mapper.UserMapper.mapToUser;
import static ru.practicum.shareit.user.mapper.UserMapper.mapToUserDto;
import static ru.practicum.shareit.utils.Pagination.*;


@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemService itemService;


    public UserDto get(Long userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null.");
        var user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User with ID #" + userId + " does not exist.");
        });
        return mapToUserDto(user);
    }

    @Override
    public ItemRequestDto save(ItemRequestDto itemRequestDto, Long requesterId) {
        validate(itemRequestDto);
        var userDto = get(requesterId);
        var user = mapToUser(userDto);
        var itemRequest = mapToItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(now());
        var save = itemRequestRepository.save(itemRequest);
        return mapToItemRequestDto(save);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Long userId) {
        List<ItemRequest> requests;
        var pageRequest = makePageRequest(from, size, Sort.by("created").descending());
        if (pageRequest == null) {
            requests = itemRequestRepository.findItemRequestByRequester_IdIsNotOrderByCreatedDesc(userId);
        } else {
            requests = itemRequestRepository.findItemRequestByRequester_IdIsNotOrderByCreatedDesc(userId, pageRequest)
                    .stream()
                    .collect(toList());
        }
        var items = itemService.getItemsByRequests(requests)
                .stream()
                .collect(groupingBy(ItemDto::getRequestId));
        return requests
                .stream()
                .map(itemRequest -> mapToItemRequestDto(itemRequest, items.get(itemRequest.getId())))
                .collect(toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId) {
        var userDto = get(userId);
        var user = mapToUser(userDto);
        var itemRequests = itemRequestRepository.findItemRequestByRequesterOrderByCreatedDesc(user);
        var items = itemService.getItemsByRequests(itemRequests)
                .stream()
                .collect(groupingBy(ItemDto::getRequestId));
        return itemRequests.stream()
                .map(itemRequest -> mapToItemRequestDto(itemRequest, items.get(itemRequest.getId())))
                .collect(toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(long requestId, Long userId) {
        mapToUser(get(userId));
        var items = itemService.getItemsByRequestId(requestId);
        var itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Request with ID#" + requestId + " does not exist"));
        return mapToItemRequestDto(itemRequest, items);
    }

    private void validate(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Request cannot be null or blank");
        }
    }
}
