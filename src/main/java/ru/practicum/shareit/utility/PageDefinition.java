package ru.practicum.shareit.utility;

import org.springframework.data.domain.PageRequest;

public class PageDefinition {
    public static PageRequest definePage(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }
}
