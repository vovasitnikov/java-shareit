package ru.practicum.shareit.user.model;

import lombok.*;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.validation.Valid;

import java.sql.SQLException;

import static javax.persistence.GenerationType.*;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = TABLE)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    @Valid
    private String email;
}
