package ru.practicum.shareit.user;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User(1, "name", "e@mail.com");
    }

    @SneakyThrows
    @Test
    void createUser_returnUser() {
        UserDto userDto = UserMapper.toUserDto(user);
        when(userService.createUser(userDto)).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(userDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void updateUser_returnUser() {
        User updUser = new User(1, "name2", "e@mail2.com");
        UserDto userDto = UserMapper.toUserDto(updUser);
        when(userService.updateUser(userDto, updUser.getId())).thenReturn(userDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", updUser.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updUser)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(userDto.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).updateUser(userDto, updUser.getId());
        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void getUserById_returnUser() {
        when(userService.getUserById(anyInt())).thenReturn(user);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", user.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(user.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(user.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(user.getEmail()), String.class))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).getUserById(user.getId());
        assertEquals(objectMapper.writeValueAsString(user), result);
    }

    @SneakyThrows
    @Test
    void getAllUsers_returnList() {
        UserDto userDto = UserMapper.toUserDto(user);
        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(user.getId()), Integer.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(user.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email", Matchers.is(user.getEmail()), String.class));

        verify(userService).getAllUsers();
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        int userId = 1;
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }

}
