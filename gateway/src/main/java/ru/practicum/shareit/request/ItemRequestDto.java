package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class ItemRequestDto {

    private Integer id;

    @NotNull(message = "Не указано описание запроса")
    @NotBlank(message = "Не указано описание запроса")
    private String description;

    private Integer requesterId;
    private LocalDateTime created;
    private List<ItemDto> items;

}
