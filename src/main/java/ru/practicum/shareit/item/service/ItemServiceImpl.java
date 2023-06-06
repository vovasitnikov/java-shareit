package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryHashMap;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.enums.BookingTimeState.PAST;
import static ru.practicum.shareit.item.mapper.CommentMapper.mapToComment;
import static ru.practicum.shareit.item.mapper.CommentMapper.mapToCommentDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static ru.practicum.shareit.user.mapper.UserMapper.mapToUser;
import static ru.practicum.shareit.utils.Pagination.makePageRequest;

@Slf4j
@Service
@AllArgsConstructor
///@Transactional(readOnly = true)
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
    public ItemAllFieldsDto get(Long id, Long userId) {
        var item = itemRepositoryHashMap.findById(id);
        if (item == null) new NotFoundException("Item with id#" + id + " does not exist");
        var comments = getAllComments(id);
        var bookings = bookingService.getBookingsByItem(item.getId(), userId);
        return mapToItemAllFieldsDto(item,
                getLastItem(bookings),
                getNextItem(bookings),
                comments);
    }

    @Override
    public void delete(Long id) {
        itemRepositoryHashMap.deleteById(id);
    }

    @Override
    public List<ItemDto> getAllItems(Long userId, Integer from, Integer size) {
//        Stream<Item> stream;
        if (userId == null) throw new ValidationException("User ID cannot be null");
        List<Item> items = itemRepositoryHashMap.getAll().stream().filter(item -> item.getOwner().getId().equals(userId)).collect(toList());
        List<ItemDto> result = new ArrayList<>();
        items.forEach(i -> result.add(mapToItemDto(i)));
//        var bookings = bookingService.getBookingsByOwnerId(userId, null)
//                .stream()
//                .collect(groupingBy((BookingAllFieldsDto bookingAllFieldsDto) -> bookingAllFieldsDto.getItem().getId()));
//        var comments = getAllComments().stream()
//                .collect(groupingBy(CommentDto::getItemId));
//        var pageRequest = makePageRequest(from, size, Sort.by("id").ascending());
//        if (pageRequest == null)
//            stream = itemRepositoryHashMap.findAllByOwner_IdIs(userId).stream();
//        else
//            stream = itemRepositoryHashMap.findAllByOwner_IdIs(userId, pageRequest).stream();
//        return stream.map(item -> ItemMapper.mapToItemAllFieldsDto(item,
//                        getLastItem(bookings.get(item.getId())),
//                        getNextItem(bookings.get(item.getId())),
//                        comments.get(item.getId())))
//                .collect(toList());
        return result;
    }

    @Override
    public List<ItemDto> search(String text, Long userId, Integer from, Integer size) {
//        Stream<Item> stream;
        if (userId == null) throw new ValidationException("User ID cannot be null");
        if (text.isBlank()) return emptyList();
        List<Item> items = itemRepositoryHashMap.getAll()
                .stream()
//                .filter(item -> item.getOwner().getId().equals(userId))
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable())
                .collect(toList());
        List<ItemDto> result = new ArrayList<>();
        items.forEach(i -> result.add(mapToItemDto(i)));
//        var pageRequest = makePageRequest(from, size, Sort.by("id").ascending());
//        if (pageRequest == null)
//            stream = itemRepositoryHashMap.search(text).stream();
//        else
//            stream = itemRepositoryHashMap.search(text, pageRequest).stream();
//        return stream
//                .map(ItemMapper::mapToItemDto)
//                .collect(toList());
        return result;
    }

    @Override
    public CommentDto saveComment(CommentDto commentDto,
                                  Long itemId,
                                  Long userId) throws NotFoundException {
        if (commentDto.getText() == null || commentDto.getText().isBlank())
            throw new ValidationException("Comment text cannot be blank");
        var item = itemRepositoryHashMap.findById(itemId);
        if (item == null) throw new NotFoundException("Item with id#" + itemId + " does not exist");
        var user = mapToUser(userService.get(userId));
        var bookings = bookingService.getAllBookings(userId, PAST.name());
        if (bookings.isEmpty()) throw new ValidationException("User cannot make comments");
        var comment = mapToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(now());
        //var save = commentRepositoryHashMap.save(comment);
        return mapToCommentDto(null);
    }

    @Override
    public List<CommentDto> getAllComments() {
        return null;
//        return commentRepositoryHashMap.findAll()
//                .stream()
//                .map(CommentMapper::mapToCommentDto)
//                .collect(toList());
    }

    @Override
    public List<CommentDto> getAllComments(Long itemId) {
        return null;
//        return commentRepositoryHashMap.findCommentByItem_IdIsOrderByCreated(itemId)
//                .stream()
//                .map(CommentMapper::mapToCommentDto)
//                .collect(toList());
    }

    @Override
    public List<ItemDto> getItemsByRequestId(Long requestId) {
        return null;
//        return itemRepositoryHashMap.findAllByRequest_IdIs(requestId)
//                .stream()
//                .map(ItemMapper::mapToItemDto)
//                .collect(toList());
    }

    @Override
    public List<ItemDto> getItemsByRequests(List<ItemRequest> requests) {
        return null;
//        return itemRepositoryHashMap.findAllByRequestIn(requests)
//                .stream()
//                .map(ItemMapper::mapToItemDto)
//                .collect(toList());
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

