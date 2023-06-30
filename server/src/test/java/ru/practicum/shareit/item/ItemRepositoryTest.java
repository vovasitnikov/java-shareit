package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;

import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        ItemDto itemDto = ItemDto.builder().name("name").description("description").available(true).build();
        itemRepository.save(ItemMapper.toItem(itemDto));
    }

    @Test
    void findAllByTextContaining() {
        String text = "des";
        PageRequest page = PageRequest.of(0, 5);

        List<Item> items = itemRepository.findAllByTextContaining(text, page).toList();
        TypedQuery<Item> query = em.getEntityManager().createQuery(
                "SELECT it FROM Item it " +
                        "WHERE UPPER(it.name) LIKE UPPER(CONCAT('%', :name, '%')) " +
                        "OR UPPER(it.description) LIKE UPPER(CONCAT('%', :description, '%')) " +
                        "AND it.isAvailable = true", Item.class);
        List<Item> itemList = query.setParameter("name", text).setParameter("description", text).getResultList();

        assertThat(items, hasSize(1));
        assertThat(items, equalTo(itemList));
    }

}
