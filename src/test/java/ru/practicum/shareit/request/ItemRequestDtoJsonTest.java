package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @SneakyThrows
    @Test
    void testItemRequestDto_returnItemRequestDto() {
        LocalDateTime time = LocalDateTime.now().plusNanos(1);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("descript").requesterId(1).created(time).build();
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.requesterId");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("descript");
        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(time.toString());
    }

}
