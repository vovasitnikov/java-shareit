package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;


public interface ItemService {

    ItemDto addItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto);

    ItemDtoBooking getItemDtoBookingById(Integer itemId, Integer userId);

    Item getItemById(Integer itemId);

    List<ItemDtoBooking> getItemsByOwner(Integer userId, int from, int size);

    List<ItemDto> searchItemByText(String text, int from, int size);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);

}
