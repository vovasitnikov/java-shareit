package ru.practicum.shareit.item.comment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    private User author;

    private LocalDateTime created;

    public Comment(Integer id, String text, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.created = created;
    }

    public Comment(Integer id, String text, Item item, User author) {
        this.id = id;
        this.text = text;
        this.item = item;
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        final Comment comment = (Comment) o;
        if (!comment.getId().equals(getId())) return false;
        if (!comment.getText().equals(getText())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = getText().hashCode();
        result = 11 * result + getId();
        return result;
    }

}