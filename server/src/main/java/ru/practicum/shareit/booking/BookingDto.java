package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingDto {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Item item;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User booker;

    private BookingStatus status;

}