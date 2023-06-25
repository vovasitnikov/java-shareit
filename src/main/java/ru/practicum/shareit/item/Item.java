package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;                                                    // @Column не указан

    @NotNull(message = "Не указано описание вещи")
    private String description;                                             // @Column не указан

    @ManyToOne(fetch = FetchType.LAZY)                                      // связь когда множество this объектов связаны с одним объектом из данного поля
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ToString.Exclude
    private User owner;

    @Column(name = "is_available")
    @NotNull(message = "Не указана доступность вещи")
    private Boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ToString.Exclude
    private ItemRequest request;

    public Item(Integer id, String name, String description, Boolean isAvailable, ItemRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.request = request;
    }

    public Item(Integer id, String name, String description, Boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
    }

    public Item(Integer id, String name, String description, User owner, Boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.isAvailable = isAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Item item = (Item) o;
        return id != null && Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
