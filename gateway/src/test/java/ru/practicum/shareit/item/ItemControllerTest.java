package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@ContextConfiguration(classes = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    private ItemDto itemDto;

    @BeforeEach
    void setup() {
        itemDto = ItemDto.builder().id(1).name("name").description("desc").available(true).build();
    }

    @SneakyThrows
    @Test
    void addItem_returnItem() {
        ResponseEntity<Object> entity = new ResponseEntity<>(itemDto, HttpStatus.OK);
        when(itemClient.addItem(1, itemDto)).thenReturn(entity);

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
    void addItem_itemNotValid() {
        itemDto.setName("");

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(1, itemDto);
    }

    @SneakyThrows
    @Test
    void updateItem_returnItemDto() {
        ItemDto updItemDto = ItemDto.builder().id(1).name("name2").description("2").available(false).build();
        ResponseEntity<Object> entity = new ResponseEntity<>(itemDto, HttpStatus.OK);
        when(itemClient.updateItem(1, updItemDto.getId(), itemDto)).thenReturn(entity);

        String result = mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", updItemDto.getId())
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

        verify(itemClient).updateItem(1, updItemDto.getId(), itemDto);
        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void getItemById_returnItemDtoBooking() {
        ResponseEntity<Object> entity = new ResponseEntity<>(itemDto, HttpStatus.OK);
        when(itemClient.getItemById(anyInt(), anyInt())).thenReturn(entity);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(itemDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(itemDto.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemDto.getDescription()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(itemDto.getAvailable()), Boolean.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemClient).getItemById(anyInt(), anyInt());
        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void getItemsByOwner_returnItemDtoBookingList() {
        ResponseEntity<Object> entity = new ResponseEntity<>(List.of(itemDto), HttpStatus.OK);
        when(itemClient.getItemsByOwner(anyLong(), anyInt(), anyInt())).thenReturn(entity);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(itemDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(itemDto.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is(itemDto.getDescription()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available", Matchers.is(itemDto.getAvailable()), Boolean.class));

        verify(itemClient).getItemsByOwner(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void searchItemByText_returnItemDtoList() {
        ResponseEntity<Object> entity = new ResponseEntity<>(List.of(itemDto), HttpStatus.OK);
        when(itemClient.searchItemByText(anyString(), anyInt(), anyInt())).thenReturn(entity);

        mockMvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(itemDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(itemDto.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is(itemDto.getDescription()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available", Matchers.is(itemDto.getAvailable()), Boolean.class));

        verify(itemClient).searchItemByText(anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void addComment_returnComment() {
        CommentDto commentDto = CommentDto.builder().id(1).text("comment").authorName("author").created(LocalDateTime.now()).build();
        ResponseEntity<Object> entity = new ResponseEntity<>(commentDto, HttpStatus.OK);
        when(itemClient.addComment(anyInt(), anyInt(), any())).thenReturn(entity);

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
