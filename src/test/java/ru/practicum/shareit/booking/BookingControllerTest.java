package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private Booking booking;
    private BookingDto bookingDto;
    private BookingItemDto bookingItemDto;

    @BeforeEach
    void setup() {
        User user = new User(1, "name", "e@mail.ya");
        Item item = new Item(1, "name", "desc", true);
        booking = new Booking(1,
                LocalDateTime.now().plusHours(1).plusNanos(1),
                LocalDateTime.now().plusHours(2).plusNanos(1),
                item, user, BookingStatus.WAITING);
        bookingItemDto = BookingMapper.toBookingItemDto(booking);
        bookingDto = BookingMapper.toBookingDto(booking);
    }

    @SneakyThrows
    @Test
    void createBooking_returnBooking() {
        when(bookingService.createBooking(1, bookingItemDto)).thenReturn(bookingDto);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingItemDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), Item.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), User.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @SneakyThrows
    @Test
    void responseToBooking_returnBooking() {
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.responseToBooking(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), Item.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), User.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), BookingStatus.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @SneakyThrows
    @Test
    void getBookingById_returnBooking() {
        when(bookingService.getBookingById(anyInt(), anyInt())).thenReturn(bookingDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), Item.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), User.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @SneakyThrows
    @Test
    void getBookingsForUser_returnBookingList() {
        when(bookingService.getBookingsForUser(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), Item.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), User.class));

        verify(bookingService).getBookingsForUser(anyInt(), anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getBookingsForOwner_returnBookingList() {
        when(bookingService.getBookingsForOwner(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), Item.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), User.class));

        verify(bookingService).getBookingsForOwner(anyInt(), anyString(), anyInt(), anyInt());
    }

}