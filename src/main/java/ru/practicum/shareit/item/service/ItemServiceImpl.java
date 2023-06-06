package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryHashMap;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static ru.practicum.shareit.user.mapper.UserMapper.mapToUser;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryHashMap itemRepositoryHashMap;
    private final BookingService bookingService;
    private final UserService userService;

    @Override
    public ItemDto save(ItemDto itemDto, ItemRequestDto itemRequestDto, Long userId) {
        validate(itemDto);
        var user = mapToUser(userService.get(userId));
        var item = mapToItem(itemDto);
        item.setOwner(user);
        if (itemRequestDto != null)
            item.setRequest(ItemRequestMapper.mapToItemRequest(
                    itemRequestDto, userService.get(itemRequestDto.getRequesterId())));
        var save = itemRepositoryHashMap.save(item);
        return mapToItemDto(save);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null");
        var item = itemRepositoryHashMap.findById(itemDto.getId());
        if (item == null) throw new NotFoundException("Item with id#" + itemDto.getId() + " does not exist");
        if (!item.getOwner().getId().equals(userId))
            throw new NotFoundException("Item has another user");
        if (itemDto.getName() != null)
            item.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            item.setAvailable(itemDto.getAvailable());
        var save = itemRepositoryHashMap.update(item);
        return mapToItemDto(save);
    }


    @Override
    public void delete(Long id) {
        itemRepositoryHashMap.deleteById(id);
    }

    @Override
    public List<ItemDto> getAllItems(Long userId, Integer from, Integer size) {
        if (userId == null) throw new ValidationException("User ID cannot be null");
        List<Item> items = itemRepositoryHashMap.getAll().stream().filter(item -> item.getOwner().getId().equals(userId)).collect(toList());
        List<ItemDto> result = new ArrayList<>();
        items.forEach(i -> result.add(mapToItemDto(i)));
        return result;
    }

    @Override
    public ItemDto get(Long userId, Long itemId) {
        return mapToItemDto(itemRepositoryHashMap.findById(itemId));
    }

    @Override
    public List<ItemDto> search(String text, Long userId, Integer from, Integer size) {
        if (userId == null) throw new ValidationException("User ID cannot be null");
        if (text.isBlank()) return emptyList();
        List<Item> items = itemRepositoryHashMap.getAll()
                .stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable())
                .collect(toList());
        List<ItemDto> result = new ArrayList<>();
        items.forEach(i -> result.add(mapToItemDto(i)));
        return result;
    }

    @Override
    public List<ItemDto> getItemsByRequestId(Long requestId) {
        return null;
    }

    @Override
    public List<ItemDto> getItemsByRequests(List<ItemRequest> requests) {
        return null;
    }

    private BookingAllFieldsDto getNextItem(List<BookingAllFieldsDto> bookings) {
        if (bookings != null)
            return bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now()))
                    .min(comparing(BookingAllFieldsDto::getEnd))
                    .orElse(null);
        else
            return null;
    }

    private BookingAllFieldsDto getLastItem(List<BookingAllFieldsDto> bookings) {
        if (bookings != null)
            return bookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(now()))
                    .max(comparing(BookingAllFieldsDto::getEnd))
                    .orElse(null);
        else
            return null;
    }

    private void validate(ItemDto item) {
        if (item.getName() == null || item.getName().isBlank())
            throw new ValidationException("Name cannot be blank");
        if (item.getDescription() == null || item.getDescription().isBlank())
            throw new ValidationException("Description cannot be blank");
        if (item.getAvailable() == null)
            throw new ValidationException("Available cannot be null");
    }
}