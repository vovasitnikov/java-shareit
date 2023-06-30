package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {

	private long itemId;

	@NotNull(message = "Нужно указать дату начала бронирования")
	@FutureOrPresent
	private LocalDateTime start;

	@NotNull(message = "Нужно указать дату конца бронирования")
	@Future
	private LocalDateTime end;
}