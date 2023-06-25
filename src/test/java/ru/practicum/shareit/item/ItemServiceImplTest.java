package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock                                            // создание мока для тестового класса
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserServiceImpl userService;
    @InjectMocks                                    // поле для внедрения моков (объект тестируемого класса)
    private ItemServiceImpl itemService;
    private Item item;
    private User user;

    @BeforeEach
    void setup() {
        user = new User(1, "name", "e@mail.ya");
        item = new Item(1, "name", "desc", true);
    }

    @Test
    void addItem_returnItem() {
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(itemRepository.save(item)).thenReturn(item);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        ItemDto newItemDto = itemService.addItem(user.getId(), itemDto);

        assertEquals(itemDto, newItemDto);
        verify(itemRepository).save(item);
    }

    @Test
    void addItem_nullName_returnException() {
        item.setName(null);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        ValidationException noNameItem = assertThrows(
                ValidationException.class,
                () -> itemService.addItem(user.getId(), itemDto));
        assertEquals("Недостаточно данных для создания вещи", noNameItem.getMessage());
    }

    @Test
    void updateItem_returnUpdatedItem() {
        int itemId = 1;
        item.setOwner(user);
        Item updItem = new Item(itemId, "newname", "newdesc", false);
        when(itemRepository.getReferenceById(itemId)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        ItemDto itemDto = ItemMapper.toItemDto(updItem);

        ItemDto newItemDto = itemService.updateItem(1, itemId, itemDto);

        assertEquals(itemDto, newItemDto);
        verify(itemRepository).save(item);
    }

    @Test
    void updateItem_wrongItem() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        doThrow(NotFoundException.class).when(itemRepository).getReferenceById(anyInt());

        assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(1, 1, itemDto));
        verify(itemRepository, never()).save(item);
    }

    @Test
    void updateItem_wrongOwner() {
        item.setOwner(user);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(itemRepository.getReferenceById(anyInt())).thenReturn(item);

        assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(2, 1, itemDto));
        verify(itemRepository, never()).save(item);
    }

    @Test
    void getItemById_returnItem() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        Item gotItem = itemService.getItemById(1);

        assertEquals(item, gotItem);
    }

    @Test
    void getItemById_wrongId() {
        int itemId = 0;
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        NotFoundException nonItem = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(itemId));
        assertEquals("Такой объект не найден", nonItem.getMessage());
    }

    @Test
    void getItemDtoBookingById_returnItemDtoBooking() {
        int itemId = 1;
        item.setOwner(user);
        List<Comment> commentList = List.of(new Comment(1, "text", item, user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(commentList);

        ItemDtoBooking itemDtoBooking = itemService.getItemDtoBookingById(itemId, 2);
        ItemDtoBooking itemDtoBooking2 = ItemMapper.toItemDtoBooking(item);
        List<CommentDto> commDtoList = commentList.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDtoBooking2.setComments(commDtoList);

        assertEquals(itemDtoBooking, itemDtoBooking2);
    }

    @Test
    void getItemsByOwner_returnOwnerItemList() {
        int from = 0;
        int size = 5;
        List<Item> itemList = List.of(item);
        PageRequest page = PageRequest.of(from, size);
        Page<Item> itemPage = new PageImpl<>(itemList);
        when(itemRepository.findAllByOwnerId(anyInt(), eq(page))).thenReturn(itemPage);
        when(bookingRepository.findLastBookingForItem(anyInt(), any())).thenReturn(List.of());
        when(bookingRepository.findNextBookingForItem(anyInt(), any())).thenReturn(List.of());
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(List.of());

        List<ItemDtoBooking> list = itemService.getItemsByOwner(1, from, size);
        List<ItemDtoBooking> list2 = itemList.stream()
                .map(ItemMapper::toItemDtoBooking)
                .peek(item -> item.setComments(List.of()))
                .collect(Collectors.toList());

        assertEquals(list, list2);
    }

    @Test
    void getItemsByOwner_notEmptyBookings_returnOwnerItemList() {
        int from = 0;
        int size = 5;
        Booking booking = new Booking(1,
                LocalDateTime.now().minus(3, ChronoUnit.HOURS),
                LocalDateTime.now().minus(2, ChronoUnit.HOURS),
                item, user, BookingStatus.APPROVED);
        List<Item> itemList = List.of(item);
        PageRequest page = PageRequest.of(from, size);
        Page<Item> itemPage = new PageImpl<>(itemList);
        when(itemRepository.findAllByOwnerId(anyInt(), eq(page))).thenReturn(itemPage);
        when(bookingRepository.findLastBookingForItem(anyInt(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findNextBookingForItem(anyInt(), any())).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(List.of());

        List<ItemDtoBooking> list = itemService.getItemsByOwner(1, from, size);
        List<ItemDtoBooking> list2 = itemList.stream()
                .map(ItemMapper::toItemDtoBooking)
                .peek(item -> item.setComments(List.of()))
                .peek(item -> item.setLastBooking(BookingMapper.toBookingItemDto(booking)))
                .peek(item -> item.setNextBooking(BookingMapper.toBookingItemDto(booking)))
                .collect(Collectors.toList());

        assertEquals(list, list2);
    }

    @Test
    void searchItemByText_returnItemList() {
        int from = 0;
        int size = 5;
        List<Item> itemList = List.of(item);
        PageRequest page = PageRequest.of(from, size);
        Page<Item> itemPage = new PageImpl<>(itemList);
        when(itemRepository.findAllByTextContaining(anyString(), eq(page))).thenReturn(itemPage);

        List<ItemDto> list = itemService.searchItemByText("text", from, size);
        List<ItemDto> list2 = itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        assertEquals(list, list2);
    }

    @Test
    void searchItemByText_emptyText_returnEmptyList() {
        int from = 0;
        int size = 5;

        List<ItemDto> list = itemService.searchItemByText("", from, size);

        assertTrue(list.isEmpty());
        verify(itemRepository, never()).findAllByTextContaining(anyString(), any());
    }

    @Test
    void addComment_returnComment() {
        Booking booking = new Booking(1,
                LocalDateTime.now().minus(3, ChronoUnit.HOURS),
                LocalDateTime.now().minus(2, ChronoUnit.HOURS),
                item, user, BookingStatus.APPROVED);
        Comment comment = new Comment(1, "text", item, user);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        when(bookingRepository.findBookingByUserAndItem(anyInt(), anyInt()))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto commentDto1 = itemService.addComment(1, 1, commentDto);

        assertEquals(commentDto, commentDto1);
    }

    @Test
    void addComment_emptyComment_returnException() {
        Comment comment = new Comment(1, "", item, user);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        ValidationException noNameItem = assertThrows(
                ValidationException.class,
                () -> itemService.addComment(1, 1, commentDto));
        assertEquals("Комментарий не может быть пустым", noNameItem.getMessage());
    }

    @Test
    void addComment_emptyBooking_returnException() {
        Comment comment = new Comment(1, "comment", item, user);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        when(bookingRepository.findBookingByUserAndItem(anyInt(), anyInt()))
                .thenReturn(List.of());

        ValidationException noNameItem = assertThrows(
                ValidationException.class,
                () -> itemService.addComment(1, 1, commentDto));
        assertEquals("Пользователь не может оставить комментарий для объекта, который не использовал", noNameItem.getMessage());
    }

}
