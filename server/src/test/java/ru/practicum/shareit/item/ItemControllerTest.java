package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.comment.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private Item item;

    @BeforeEach
    void setup() {
        item = new Item(1, "name", "desc", true);
    }

    @SneakyThrows
    @Test
    void addItem_returnItem() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(itemService.addItem(1, itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(itemDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(itemDto.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemDto.getDescription()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(itemDto.getAvailable()), Boolean.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void updateItem_returnItemDto() {
        Item updItem = new Item(1, "name2", "2", false);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(itemService.updateItem(1, updItem.getId(), itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", updItem.getId())
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(itemDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(itemDto.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemDto.getDescription()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(itemDto.getAvailable()), Boolean.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).updateItem(1, updItem.getId(), itemDto);
        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void getItemById_returnItemDtoBooking() {
        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);
        when(itemService.getItemDtoBookingById(anyInt(), anyInt())).thenReturn(itemDtoBooking);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(item.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(item.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(item.getDescription()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(item.getIsAvailable()), Boolean.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getItemDtoBookingById(anyInt(), anyInt());
        assertEquals(objectMapper.writeValueAsString(itemDtoBooking), result);
    }

    @SneakyThrows
    @Test
    void getItemsByOwner_returnItemDtoBookingList() {
        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);
        when(itemService.getItemsByOwner(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemDtoBooking));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(itemDtoBooking.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(itemDtoBooking.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is(itemDtoBooking.getDescription()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available", Matchers.is(itemDtoBooking.getAvailable()), Boolean.class));

        verify(itemService).getItemsByOwner(anyInt(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void searchItemByText_returnItemDtoList() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(itemService.searchItemByText(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(itemDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(itemDto.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is(itemDto.getDescription()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available", Matchers.is(itemDto.getAvailable()), Boolean.class));

        verify(itemService).searchItemByText(anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void addComment_returnComment() {
        CommentDto commentDto = CommentDto.builder().id(1).text("comment").authorName("author").created(LocalDateTime.now()).build();
        when(itemService.addComment(anyInt(), anyInt(), eq(commentDto))).thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(commentDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text", Matchers.is(commentDto.getText()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorName", Matchers.is(commentDto.getAuthorName()), String.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
    }

}
