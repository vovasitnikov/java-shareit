package ru.practicum.shareit.booking;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        return new BookingItemDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus().toString());
    }

    public static Booking toBooking(BookingItemDto bookingItemDto) {
        return Booking.builder()
                .id(bookingItemDto.getId())
                .start(bookingItemDto.getStart())
                .end(bookingItemDto.getEnd())
                .build();
    }
}