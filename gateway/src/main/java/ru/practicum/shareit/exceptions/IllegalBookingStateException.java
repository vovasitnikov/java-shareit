package ru.practicum.shareit.exceptions;

public class IllegalBookingStateException extends RuntimeException {
    public IllegalBookingStateException(String message) {
        super(message);
    }
}