package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utility.PageDefinition;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(Integer userId, ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty()
                || itemDto.getDescription() == null || itemDto.getAvailable() == null) {
            log.warn("Недостаточно данных для создания вещи");
            throw new ValidationException("Недостаточно данных для создания вещи");
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userService.getUserById(userId));
        //item.setOwner(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не существует")));
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.getReferenceById(itemDto.getRequestId()));
        }
        Item newItem = itemRepository.save(item);
        log.info("Создана вещь id={}", newItem.getId());

        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item existedItem = itemRepository.getReferenceById(itemId);
        if (!existedItem.getOwner().getId().equals(userId)) {
            log.warn("Объект принадлежит другому пользователю");
            throw new NotFoundException("Объект принадлежит другому пользователю");
        }
        if (itemDto.getName() != null) {
            existedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existedItem.setIsAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequestId() != null) {
            existedItem.setRequest(itemRequestRepository.getReferenceById(itemDto.getRequestId()));
        }
        Item updItem = itemRepository.save(existedItem);
        log.info("Обновлена вещь id={}", itemId);

        return ItemMapper.toItemDto(updItem);
    }

    @Override
    public ItemDtoBooking getItemDtoBookingById(Integer itemId, Integer userId) {
        Item item = getItemById(itemId);
        ItemDtoBooking itemDtoBooking = setCommentsToItem(item);
        log.info("Вызвана вещь id={}", itemId);
        if (userId.equals(item.getOwner().getId())) {
            return setBookingsToItem(itemDtoBooking);
        }
        return itemDtoBooking;
    }

    @Override
    public Item getItemById(Integer itemId) {
        log.info("Вызвана вещь id={}", itemId);

        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Объект не найден");
                    return new NotFoundException("Такой объект не найден");
                });
    }

    @Override
    public List<ItemDtoBooking> getItemsByOwner(Integer userId, int from, int size) {
        log.info("Вызван список вещей для пользователя id ={}", userId);

        return itemRepository.findAllByOwnerId(userId, PageDefinition.definePage(from, size))
                .stream()
                .map(this::setCommentsToItem)
                .map(this::setBookingsToItem)
                .sorted(Comparator.comparing(item -> {
                    if (item.getNextBooking() == null) {
                        return LocalDateTime.MIN;
                    }
                    return item.getNextBooking().getStart();
                }, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    private ItemDtoBooking setBookingsToItem(ItemDtoBooking itemDtoBooking) {
        List<Booking> lastBooking = bookingRepository.findLastBookingForItem(itemDtoBooking.getId(), LocalDateTime.now())
                .stream()
                .filter(book -> book.getStatus() != BookingStatus.REJECTED)
                .collect(Collectors.toList());
        if (!lastBooking.isEmpty()) {
            BookingItemDto lastBookingItemDto = BookingMapper.toBookingItemDto(lastBooking.get(0));
            itemDtoBooking.setLastBooking(lastBookingItemDto);
        }
        List<Booking> nextBooking = bookingRepository.findNextBookingForItem(itemDtoBooking.getId(), LocalDateTime.now())
                .stream()
                .filter(book -> book.getStatus() != BookingStatus.REJECTED)
                .collect(Collectors.toList());
        if (!nextBooking.isEmpty()) {
            BookingItemDto nextBookingItemDto = BookingMapper.toBookingItemDto(nextBooking.get(0));
            itemDtoBooking.setNextBooking(nextBookingItemDto);
        }
        return itemDtoBooking;
    }

    private ItemDtoBooking setCommentsToItem(Item item) {
        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);
        List<CommentDto> commentDtoList = commentRepository.findAllByItemId(item.getId())
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDtoBooking.setComments(commentDtoList);

        return itemDtoBooking;
    }

    @Override
    public List<ItemDto> searchItemByText(String text, int from, int size) {
        if (text.isEmpty() || text.isBlank()) {
            log.warn("Вызван поиск вещей для пустой строки");
            return new ArrayList<>();
        }
        log.info("Вызван список вещей по строке поиска \"{}\"", text);

        return itemRepository.findAllByTextContaining(text.toLowerCase(), PageDefinition.definePage(from, size))
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            log.info("Комментарий не может быть пустым");
            throw new ValidationException("Комментарий не может быть пустым");
        }
        List<Booking> booking = bookingRepository.findBookingByUserAndItem(itemId, userId)
                .stream()
                .filter(book -> book.getEnd().isBefore(LocalDateTime.now()))
                .filter(book -> book.getStatus() != BookingStatus.REJECTED)
                .collect(Collectors.toList());
        if (booking.isEmpty()) {
            log.info("Пользователь не может оставить комментарий для объекта, который не использовал");
            throw new ValidationException("Пользователь не может оставить комментарий для объекта, который не использовал");
        } else {
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setItem(booking.get(0).getItem());
            comment.setAuthor(booking.get(0).getBooker());
            comment.setCreated(LocalDateTime.now());

            return CommentMapper.toCommentDto(commentRepository.save(comment));
        }
    }

}
