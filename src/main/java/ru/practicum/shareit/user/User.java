package ru.practicum.shareit.user;

import lombok.*;


import javax.persistence.*;


@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        final User user = (User) o;
        if (!user.getId().equals(getId())) return false;
        if (!user.getName().equals(getName())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = getName().hashCode();
        result = 11 * result + getId();
        return result;
    }
}