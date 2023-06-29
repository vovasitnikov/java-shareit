package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class ItemRequestDto {

    private Integer id;
    private String description;
    private Integer requesterId;
    private LocalDateTime created;
    private List<ItemDto> items;

}