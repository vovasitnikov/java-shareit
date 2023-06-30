package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;

    @Autowired
    private JacksonTester<BookingItemDto> jsonBookingItemDto;

    private final LocalDateTime start = LocalDateTime.now().plusNanos(1);
    private final LocalDateTime end = LocalDateTime.now().plusNanos(2);
    private final User user = new User(1, "name", "email");
    private final Item item = new Item(1, "name", "desc", user, true);

    @SneakyThrows
    @Test
    void testBookingDto_returnBookingDto() {
        BookingDto bookingDto = BookingDto.builder().start(start).end(end).item(item).booker(user).status(BookingStatus.WAITING).build();
        JsonContent<BookingDto> result = jsonBookingDto.write(bookingDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(item.getId());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(user.getId());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @SneakyThrows
    @Test
    void testBookingItemDto_returnBookingItemDto() {
        BookingItemDto bookingItemDto = BookingItemDto.builder().start(start).end(end).itemId(1).bookerId(1).status("WAITING").build();
        JsonContent<BookingItemDto> result = jsonBookingItemDto.write(bookingItemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

}
