package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingItemDto;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Data
@Builder
public class ItemDtoBooking {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private List<CommentDto> comments;
    private Integer requestId;

}
