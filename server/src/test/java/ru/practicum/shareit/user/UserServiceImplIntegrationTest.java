package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserService userService;
    private final UserRepository userRepository;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        userDto = UserDto.builder().name("name").email("e@mail.com").build();
    }

    @Test
    void createUser_returnSavedUser() {
        UserDto createdUserDto = userService.createUser(userDto);
        int id = createdUserDto.getId();
        User user = userRepository.getReferenceById(id);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser_returnUpdatedUser() {
        UserDto createdUserDto = userService.createUser(userDto);
        UserDto userDto = UserDto.builder().name("newName").email("new@mail.com").build();
        int id = createdUserDto.getId();

        UserDto updUserDto = userService.updateUser(userDto, id);
        User user = userRepository.getReferenceById(id);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(updUserDto.getName()));
        assertThat(user.getEmail(), equalTo(updUserDto.getEmail()));
    }

    @Test
    void getUserById_returnUser() {
        UserDto userDto = userService.createUser(this.userDto);
        int id = userDto.getId();

        User userById = userService.getUserById(id);
        User user = userRepository.getReferenceById(id);

        assertThat(userById.getId(), notNullValue());
        assertThat(userById.getName(), equalTo(user.getName()));
        assertThat(userById.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void getAllUsers_returnListSize() {
        UserDto userDto2 = UserDto.builder().name("name2").email("e2@mail.com").build();
        UserDto userDto3 = UserDto.builder().name("name3").email("e3@mail.com").build();
        userService.createUser(userDto);
        userService.createUser(userDto2);
        userService.createUser(userDto3);

        List<UserDto> users = userService.getAllUsers();

        assertThat(users, hasSize(3));
        for (UserDto user : users) {
            assertThat(users, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(user.getName())),
                    hasProperty("email", equalTo(user.getEmail()))
            )));
        }
    }

    @Test
    void deleteUser() {
        UserDto userDto = userService.createUser(this.userDto);
        int id = userDto.getId();

        userService.deleteUser(id);

        assertThrows(JpaObjectRetrievalFailureException.class,
                () -> userRepository.getReferenceById(id));
    }

}
