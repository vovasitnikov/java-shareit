package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private Booking booking;
    private PageRequest page;
    private User user1;
    private User user2;
    private Item item;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(1, "name", "e@mail.ya"));
        user2 = userRepository.save(new User(2, "name2", "e2@mail.ya"));
        item = itemRepository.save(new Item(1, "name", "desc", true));
        item.setOwner(user2);
        booking = new Booking(1,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3),
                item, user1, BookingStatus.WAITING);
        page = PageRequest.of(0, 5);
    }

    @AfterEach
    void drop() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void getBookingsForOwner_returnOwnerBookingList() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.getBookingsForOwner(user2.getId(), page).toList();

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(bookings.get(0).getItem(), equalTo(booking.getItem()));
        assertThat(bookings.get(0).getBooker(), equalTo(booking.getBooker()));
        assertThat(bookings.get(0).getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void getBookingsForOwnerByStatus_returnOwnerBookingList() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.getBookingsForOwnerByStatus(user2.getId(), "WAITING", page).toList();

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(bookings.get(0).getItem(), equalTo(booking.getItem()));
        assertThat(bookings.get(0).getBooker(), equalTo(booking.getBooker()));
        assertThat(bookings.get(0).getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void getBookingsForOwnerCurrent_returnOwnerBookingList() throws InterruptedException {
        booking.setStart(LocalDateTime.now().plusNanos(100_000_000));
        bookingRepository.save(booking);
        Thread.sleep(100);

        List<Booking> bookings = bookingRepository.getBookingsForOwnerCurrent(user2.getId(), LocalDateTime.now(), LocalDateTime.now(), page).toList();

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(bookings.get(0).getItem(), equalTo(booking.getItem()));
        assertThat(bookings.get(0).getBooker(), equalTo(booking.getBooker()));
        assertThat(bookings.get(0).getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void getBookingsForOwnerPast_returnOwnerBookingList() throws InterruptedException {
        booking.setStart(LocalDateTime.now().plusNanos(100_000_000));
        booking.setEnd(LocalDateTime.now().plusNanos(200_000_000));
        bookingRepository.save(booking);
        Thread.sleep(300);

        List<Booking> bookings = bookingRepository.getBookingsForOwnerPast(user2.getId(), LocalDateTime.now(), page).toList();

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(bookings.get(0).getItem(), equalTo(booking.getItem()));
        assertThat(bookings.get(0).getBooker(), equalTo(booking.getBooker()));
        assertThat(bookings.get(0).getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void getBookingsForOwnerFuture_returnOwnerBookingList() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.getBookingsForOwnerFuture(user2.getId(), LocalDateTime.now(), page).toList();

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(bookings.get(0).getItem(), equalTo(booking.getItem()));
        assertThat(bookings.get(0).getBooker(), equalTo(booking.getBooker()));
        assertThat(bookings.get(0).getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void findLastBookingForItem_returnItemBookingList() throws InterruptedException {
        booking.setStart(LocalDateTime.now().plusNanos(100_000_000));
        bookingRepository.save(booking);
        Thread.sleep(200);

        List<Booking> bookings = bookingRepository.findLastBookingForItem(item.getId(), LocalDateTime.now());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(bookings.get(0).getItem(), equalTo(booking.getItem()));
        assertThat(bookings.get(0).getBooker(), equalTo(booking.getBooker()));
        assertThat(bookings.get(0).getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void findNextBookingForItem_returnItemBookingList() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findNextBookingForItem(item.getId(), LocalDateTime.now());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(bookings.get(0).getItem(), equalTo(booking.getItem()));
        assertThat(bookings.get(0).getBooker(), equalTo(booking.getBooker()));
        assertThat(bookings.get(0).getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void findBookingByUserAndItem() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findBookingByUserAndItem(item.getId(), user1.getId());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(bookings.get(0).getItem(), equalTo(booking.getItem()));
        assertThat(bookings.get(0).getBooker(), equalTo(booking.getBooker()));
        assertThat(bookings.get(0).getStatus(), equalTo(booking.getStatus()));
    }

}
