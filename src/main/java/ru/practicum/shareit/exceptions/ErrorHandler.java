package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)                                         // 400
    public ErrorResponse handleValidationError(final MethodArgumentNotValidException v) {
        return new ErrorResponse(v.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)                                         // 400
    public ErrorResponse handleValidationError(final ValidationException v) {
        return new ErrorResponse(v.getMessage());
    }

    @ExceptionHandler                                                               // 404
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundError(final NotFoundException n) {
        return new ErrorResponse(n.getMessage());
    }

    @ExceptionHandler                                                               // 409
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNotFoundError(final UserAlreadyExistException n) {
        return new ErrorResponse(n.getMessage());
    }

    @ExceptionHandler                                                               // 500
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        return new ErrorResponse("Произошла непредвиденная ошибка");
    }

     class ErrorResponse {
        String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
