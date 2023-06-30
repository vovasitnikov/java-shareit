package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingItemDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.user.User;
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
public class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private ItemDto itemDto;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        userDto = userService.createUser(UserDto.builder().name("name").email("e@mail.com").build());
        itemDto = ItemDto.builder().name("name").description("desc").available(true).build();
    }

    @Test
    void addItem_returnItem() {
        ItemDto addedItemDto = itemService.addItem(userDto.getId(), itemDto);
        int id = addedItemDto.getId();
        Item item = itemRepository.getReferenceById(id);

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getIsAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void updateItem_returnUpdatedItem() {
        ItemDto createdItemDto = itemService.addItem(userDto.getId(), itemDto);
        ItemDto itemDto = ItemDto.builder().name("newName").description("newDesc").available(true).build();
        int id = createdItemDto.getId();

        ItemDto updItemDto = itemService.updateItem(userDto.getId(), id, itemDto);
        Item item = itemRepository.getReferenceById(id);

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(updItemDto.getName()));
        assertThat(item.getDescription(), equalTo(updItemDto.getDescription()));
        assertThat(item.getIsAvailable(), equalTo(updItemDto.getAvailable()));
    }

    @Test
    void getItemById_returnItem() {
        ItemDto itemDto = itemService.addItem(userDto.getId(), this.itemDto);
        int id = itemDto.getId();

        Item itemById = itemService.getItemById(id);
        Item item = itemRepository.getReferenceById(id);

        assertThat(itemById.getId(), notNullValue());
        assertThat(itemById.getName(), equalTo(item.getName()));
        assertThat(itemById.getDescription(), equalTo(item.getDescription()));
        assertThat(itemById.getIsAvailable(), equalTo(item.getIsAvailable()));
    }

    @Test
    void getItemDtoBookingById_returnItemDtoBooking() {
        ItemDto itemDto = itemService.addItem(userDto.getId(), this.itemDto);
        int id = itemDto.getId();

        ItemDtoBooking itemDtoBookingById = itemService.getItemDtoBookingById(id, userDto.getId());
        Item item = itemRepository.getReferenceById(id);

        assertThat(itemDtoBookingById.getId(), notNullValue());
        assertThat(itemDtoBookingById.getName(), equalTo(item.getName()));
        assertThat(itemDtoBookingById.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoBookingById.getAvailable(), equalTo(item.getIsAvailable()));
    }

    @Test
    void getItemsByOwner_returnOwnerItemList() {
        int from = 0;
        int size = 5;
        itemService.addItem(userDto.getId(), itemDto);
        itemService.addItem(userDto.getId(),
                ItemDto.builder().name("name2").description("desc2").available(true).build());

        List<ItemDtoBooking> items = itemService.getItemsByOwner(userDto.getId(), from, size);

        assertThat(items, hasSize(2));
        for (ItemDtoBooking item : items) {
            assertThat(items, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription()))
            )));
        }
    }

    @Test
    void searchItemByText_returnItemList() {
        int from = 0;
        int size = 5;
        ItemDto newItemDto = itemService.addItem(userDto.getId(), itemDto);

        List<ItemDto> items = itemService.searchItemByText("des", from, size);

        assertThat(items, hasSize(1));
        for (ItemDto item : items) {
            assertThat(items, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription()))
            )));
        }
        assertThat(items.get(0).getId(), equalTo(newItemDto.getId()));
        assertThat(items.get(0).getName(), equalTo(newItemDto.getName()));
        assertThat(items.get(0).getDescription(), equalTo(newItemDto.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(newItemDto.getAvailable()));
    }

    @Test
    void addComment_returnComment() throws InterruptedException {
        String user2name = "name2";
        UserDto userDto2 = userService.createUser(UserDto.builder().name(user2name).email("e2@mail.com").build());
        ItemDto newItemDto = itemService.addItem(userDto.getId(), itemDto);
        User user = UserMapper.toUser(userDto2);
        Item item = ItemMapper.toItem(newItemDto);
        BookingItemDto bookingItemDto = new BookingItemDto(1,
                LocalDateTime.now().plusNanos(100_000_000),
                LocalDateTime.now().plusNanos(200_000_000),
                newItemDto.getId(), userDto2.getId(), "APPROVED");
        bookingService.createBooking(userDto2.getId(), bookingItemDto);
        CommentDto commentDto = CommentDto.builder().text("comment").authorName(user2name).build();
        Thread.sleep(200);

        CommentDto addedComment = itemService.addComment(user.getId(), item.getId(), commentDto);

        assertThat(commentDto.getText(), equalTo(addedComment.getText()));
        assertThat(commentDto.getAuthorName(), equalTo(addedComment.getAuthorName()));
    }

}
