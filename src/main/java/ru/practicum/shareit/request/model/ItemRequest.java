package ru.practicum.shareit.request.model;

import ru.practicum.shareit.user.model.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
