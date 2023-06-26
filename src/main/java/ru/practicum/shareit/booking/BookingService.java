package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Integer userId, BookingItemDto bookingItemDto);

    BookingDto responseToBooking(Integer userId, Integer bookingId, Boolean approved);

    BookingDto getBookingById(Integer userId, Integer bookingId);

    List<BookingDto> getBookingsForUser(Integer userId, String state, int from, int size);

    List<BookingDto> getBookingsForOwner(Integer userId, String state, int from, int size);
}