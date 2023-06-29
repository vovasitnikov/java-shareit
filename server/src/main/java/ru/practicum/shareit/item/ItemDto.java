package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;

}