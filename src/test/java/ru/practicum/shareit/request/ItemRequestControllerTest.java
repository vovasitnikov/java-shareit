package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        User user = new User(1, "name", "e@mail.ya");
        ItemRequest itemRequest = new ItemRequest(1, "request", LocalDateTime.now().withNano(1), user);
        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @SneakyThrows
    @Test
    void addRequest_returnRequest() {
        when(itemRequestService.addRequest(1, itemRequestDto)).thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requesterId", is(itemRequestDto.getRequesterId()), Integer.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString()), LocalDateTime.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void getMyRequests_returnRequestList() {
        when(itemRequestService.getMyRequests(anyInt())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].requesterId", is(itemRequestDto.getRequesterId()), Integer.class))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().toString()), LocalDateTime.class));

        verify(itemRequestService).getMyRequests(anyInt());
    }

    @SneakyThrows
    @Test
    void getRequestById_returnRequest() {
        when(itemRequestService.getRequestById(anyInt(), anyInt())).thenReturn(itemRequestDto);

        String result = mockMvc.perform(get("/requests/{requestId}", itemRequestDto.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requesterId", is(itemRequestDto.getRequesterId()), Integer.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString()), LocalDateTime.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void getAllRequests_returnRequestList() {
        when(itemRequestService.getAllRequests(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].requesterId", is(itemRequestDto.getRequesterId()), Integer.class))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().toString()), LocalDateTime.class));

        verify(itemRequestService).getAllRequests(anyInt(), anyInt(), anyInt());
    }

}