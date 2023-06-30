package ru.practicum.shareit.request;

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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@ContextConfiguration(classes = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = ItemRequestDto.builder().id(1).description("request").created(LocalDateTime.now().withNano(1)).build();
    }

    @SneakyThrows
    @Test
    void addRequest_returnRequest() {
        ResponseEntity<Object> entity = new ResponseEntity<>(itemRequestDto, HttpStatus.OK);
        when(itemRequestClient.addRequest(1, itemRequestDto)).thenReturn(entity);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(itemRequestDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemRequestDto.getDescription()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requesterId", Matchers.is(itemRequestDto.getRequesterId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created", Matchers.is(itemRequestDto.getCreated().toString()), LocalDateTime.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void getMyRequests_returnRequestList() {
        ResponseEntity<Object> entity = new ResponseEntity<>(List.of(itemRequestDto), HttpStatus.OK);
        when(itemRequestClient.getMyRequests(anyInt())).thenReturn(entity);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(itemRequestDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is(itemRequestDto.getDescription()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].requesterId", Matchers.is(itemRequestDto.getRequesterId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].created", Matchers.is(itemRequestDto.getCreated().toString()), LocalDateTime.class));

        verify(itemRequestClient).getMyRequests(anyInt());
    }

    @SneakyThrows
    @Test
    void getRequestById_returnRequest() {
        ResponseEntity<Object> entity = new ResponseEntity<>(itemRequestDto, HttpStatus.OK);
        when(itemRequestClient.getRequestById(anyInt(), anyInt())).thenReturn(entity);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", itemRequestDto.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(itemRequestDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(itemRequestDto.getDescription()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requesterId", Matchers.is(itemRequestDto.getRequesterId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created", Matchers.is(itemRequestDto.getCreated().toString()), LocalDateTime.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void getAllRequests_returnRequestList() {
        ResponseEntity<Object> entity = new ResponseEntity<>(List.of(itemRequestDto), HttpStatus.OK);
        when(itemRequestClient.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(entity);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(itemRequestDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is(itemRequestDto.getDescription()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].requesterId", Matchers.is(itemRequestDto.getRequesterId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].created", Matchers.is(itemRequestDto.getCreated().toString()), LocalDateTime.class));

        verify(itemRequestClient).getAllRequests(anyLong(), anyInt(), anyInt());
    }

}
