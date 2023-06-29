package ru.practicum.shareit.item;

import ru.practicum.shareit.request.ItemRequest;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .requestId(setRequestId(item))
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
                );
    }

    public static ItemDtoBooking toItemDtoBooking(Item item) {
        return ItemDtoBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .requestId(setRequestId(item))
                .build();
    }

    private static Integer setRequestId(Item item) {
        ItemRequest request = item.getRequest();
        if (request != null) {
            return request.getId();
        }
        return null;
    }

}
