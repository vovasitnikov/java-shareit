package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Нужно указать дату начала бронирования")
    @Future(message = "Начало бронирования не может быть в прошлом")
    @Column(name = "start_date")
    private LocalDateTime start;

    @NotNull(message = "Нужно указать дату конца бронирования")
    @Future(message = "Завершение бронирования не может быть в прошлом")
    @Column(name = "end_date")
    private LocalDateTime end;

    @NotNull(message = "Нужно указать объект бронирования")
    @ManyToOne(fetch = FetchType.LAZY)                                          // связь когда множество this объектов связаны с одним объектом из данного поля
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;

    @NotNull(message = "Нужно указать пользователя-инициатора бронирования")
    @ManyToOne(fetch = FetchType.LAZY)                                          // связь когда множество this объектов связаны с одним объектом из данного поля
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "booker_id")
    @ToString.Exclude
    private User booker;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Booking booking = (Booking) o;
        return id != null && Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
