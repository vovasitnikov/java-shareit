package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private UserDto userDto1;
    private UserDto userDto2;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto preItemRequestDto;

    @BeforeEach
    void setUp() {
        userDto1 = userService.createUser(
                UserDto.builder().name("name").email("e@mail.com").build());
        userDto2 = userService.createUser(
                UserDto.builder().name("name2").email("e2@mail.com").build());
        preItemRequestDto = ItemRequestMapper.toItemRequestDto(
                new ItemRequest(1, "request", LocalDateTime.MIN, UserMapper.toUser(userDto1)));
    }

    @Test
    void addRequest_returnRequestDto() {
        itemRequestDto = itemRequestService.addRequest(userDto1.getId(), preItemRequestDto);
        int id = itemRequestDto.getId();
        ItemRequest itemRequest = itemRequestRepository.getReferenceById(id);

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(itemRequest.getRequester().getId(), equalTo(itemRequestDto.getRequesterId()));
    }

    @Test
    void getMyRequests_returnRequestDtoList() {
        itemRequestDto = itemRequestService.addRequest(userDto1.getId(), preItemRequestDto);

        List<ItemRequestDto> itemRequestList = itemRequestService.getMyRequests(userDto1.getId());

        assertThat(itemRequestList, hasSize(1));
        for (ItemRequestDto item : itemRequestList) {
            assertThat(itemRequestList, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(item.getDescription())),
                    hasProperty("created", equalTo(item.getCreated())),
                    hasProperty("requesterId", equalTo(item.getRequesterId()))
            )));
        }
    }

    @Test
    void getRequestById_returnRequestDto() {
        itemRequestDto = itemRequestService.addRequest(userDto1.getId(), preItemRequestDto);
        int id = itemRequestDto.getId();
        ItemRequest itemRequest = itemRequestRepository.getReferenceById(id);

        ItemRequestDto itemRequestById = itemRequestService.getRequestById(userDto1.getId(), id);

        assertThat(itemRequestById.getId(), notNullValue());
        assertThat(itemRequestById.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestById.getCreated(), equalTo(itemRequest.getCreated()));
        assertThat(itemRequestById.getRequesterId(), equalTo(itemRequest.getRequester().getId()));
    }

    @Test
    void getAllRequests_returnRequestDtoList() {
        int from = 0;
        int size = 5;
        itemRequestDto = itemRequestService.addRequest(userDto1.getId(), preItemRequestDto);

        List<ItemRequestDto> requestList = itemRequestService.getAllRequests(userDto2.getId(), from, size);

        assertThat(requestList, hasSize(1));
        for (ItemRequestDto request : requestList) {
            assertThat(requestList, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(request.getDescription())),
                    hasProperty("created", equalTo(request.getCreated())),
                    hasProperty("requesterId", equalTo(request.getRequesterId()))
            )));
        }
    }

}