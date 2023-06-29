package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingDto {

    private Integer id;

    @NotNull(message = "Нужно указать дату начала бронирования")
    @Future(message = "Начало бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Нужно указать дату конца бронирования")
    @Future(message = "Завершение бронирования не может быть в прошлом")
    private LocalDateTime end;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @NotNull(message = "Нужно указать объект бронирования")
    private Item item;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User booker;

    private BookingStatus status;

}
