package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;



@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Не указано описание запроса")
    @NotBlank(message = "Не указано описание запроса")
    private String description;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @NotNull(message = "Не указан инициатор запроса")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "requester_id", nullable = false)
    @ToString.Exclude
    private User requester;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemRequest)) return false;
        final ItemRequest itemRequest = (ItemRequest) o;
        if (!itemRequest.getId().equals(getId())) return false;
        if (!itemRequest.getRequester().equals(getRequester())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = getRequester().hashCode();
        result = 11 * result + getId();
        return result;
    }

}
