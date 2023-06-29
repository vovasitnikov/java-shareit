package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {

    private Integer id;

    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;

    @NotNull(message = "Не указано описание вещи")
    private String description;

    @NotNull(message = "Не указана доступность вещи")
    private Boolean available;

    private Integer requestId;

}
