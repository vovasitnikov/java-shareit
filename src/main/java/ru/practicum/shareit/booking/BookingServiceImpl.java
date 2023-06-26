package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utility.PageDefinition;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto createBooking(Integer userId, BookingItemDto bookingItemDto) {
        checkBookingDates(bookingItemDto);
        User user = userService.getUserById(userId);
        User user1 = userRepository.findById(userId).orElseThrow(throw new NotFoundException("Пользователь не может забронировать собственный предмет"));
        Item item = itemService.getItemById(bookingItemDto.getItemId());
        if (item.getOwner().equals(user)) {
            log.warn("Пользователь не может забронировать собственный предмет");
            throw new NotFoundException("Пользователь не может забронировать собственный предмет");
        }
        if (bookingItemDto.getStart().isBefore(LocalDateTime.now()) || !item.getIsAvailable()) {
            log.warn("Объект не доступен для бронирования");
            throw new ValidationException("Объект не доступен для бронирования");
        }
        Booking booking = BookingMapper.toBooking(bookingItemDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        Booking newBooking = bookingRepository.save(booking);
        log.info("Создано бронирование id={}", newBooking.getId());

        return BookingMapper.toBookingDto(newBooking);
    }

    @Override
    public BookingDto responseToBooking(Integer userId, Integer bookingId, Boolean approved) {
        Booking booking = checkBookingForExist(bookingId);
        Integer ownerId = booking.getItem().getOwner().getId();
        if (!ownerId.equals(userId)) {
            log.warn("id владельца объекта не совпадают");
            throw new NotFoundException("Только владелец объекта может подтверждать бронирование");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.warn("Повторное подтверждение");
            throw new ValidationException("Бронирование уже подтверждено");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            log.info("Бронирование id={} подтверждено", booking.getId());
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            log.info("Бронирование id={} отклонено", booking.getId());
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Integer userId, Integer bookingId) {
        Booking booking = checkBookingForExist(bookingId);
        userService.getUserById(userId);
        Integer ownerId = booking.getItem().getOwner().getId();
        Integer bookerId = booking.getBooker().getId();
        if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
            log.warn("id пользователя не соответствует участникам бронирования");
            throw new NotFoundException("Только букер или владелец объекта может просматривать бронирование");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsForUser(Integer userId, String state, int from, int size) {
        userService.getUserById(userId);
        PageRequest page = PageDefinition.definePage(from, size);
        Page<Booking> userBookings;
        switch (state) {
            case "ALL":
                userBookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page);
                break;
            case "CURRENT":
                userBookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case "PAST":
                userBookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
                break;
            case "FUTURE":
                userBookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
                break;
            case "WAITING":
            case "REJECTED":
                userBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf(state), page);
                break;
            default:
                log.warn("Некорректный статус бронирования");
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("Получен список бронирований для пользователя id={} по условию {}", userId, state);

        return userBookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsForOwner(Integer userId, String state, int from, int size) {
        userService.getUserById(userId);
        PageRequest page = PageDefinition.definePage(from, size);
        Page<Booking> ownerBookings;
        switch (state) {
            case "ALL":
                ownerBookings = bookingRepository.getBookingsForOwner(userId, page);
                break;
            case "CURRENT":
                ownerBookings = bookingRepository.getBookingsForOwnerCurrent(userId, LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case "PAST":
                ownerBookings = bookingRepository.getBookingsForOwnerPast(userId, LocalDateTime.now(), page);
                break;
            case "FUTURE":
                ownerBookings = bookingRepository.getBookingsForOwnerFuture(userId, LocalDateTime.now(), page);
                break;
            case "WAITING":
            case "REJECTED":
                ownerBookings = bookingRepository.getBookingsForOwnerByStatus(userId, state, page);
                break;
            default:
                log.warn("Некорректный статус бронирования");
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("Получен список бронирований для владельца id={} по условию {}", userId, state);

        return ownerBookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    private Booking checkBookingForExist(Integer bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Бронирование не найдено");
                    throw new NotFoundException("Такое бронирование не найдено");
                });
    }

    private void checkBookingDates(BookingItemDto bookingItemDto) {
        if (bookingItemDto.getStart().isAfter(bookingItemDto.getEnd())
                || bookingItemDto.getStart().equals(bookingItemDto.getEnd())) {
            log.warn("Даты бронирования не корректны");
            throw new ValidationException("Даты бронирования не корректны");
        }
    }

}
