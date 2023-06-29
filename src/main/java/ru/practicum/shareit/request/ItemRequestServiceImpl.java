package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utility.PageDefinition;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemRequestDto addRequest(Integer userId, ItemRequestDto itemRequestDto) {
        User user = userService.getUserById(userId);
        itemRequestDto.setRequesterId(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest savedRequest;
        try {
            ItemRequest request = ItemRequestMapper.toItemRequest(itemRequestDto);
            request.setRequester(user);
            savedRequest = itemRequestRepository.save(request);
        } catch (ConstraintViolationException e) {
            log.info("Введены некорректные данные для создания запроса");
            throw new ValidationException("Некорректные данные для создания запроса");
        }
        log.info("Создан запрос id={}", savedRequest.getId());

        return ItemRequestMapper.toItemRequestDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getMyRequests(Integer userId) {
        userService.getUserById(userId);
        log.info("Получен список запросов для пользователя id={}", userId);

        return itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .peek(itemRequestDto -> itemRequestDto.setItems(getRequestItems(itemRequestDto.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Integer userId, Integer requestId) {
        userService.getUserById(userId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(
                itemRequestRepository.findById(requestId)
                        .orElseThrow(() -> {
                            log.warn("Объект не найден");
                            return new NotFoundException("Такой объект не найден");
                        }));
        itemRequestDto.setItems(getRequestItems(requestId));
        log.info("Вызван запрос id={}", requestId);

        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Integer userId, int from, int size) {
        userService.getUserById(userId);
        log.info("Пользователем id={} вызван список других запросов", userId);

        return itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, PageDefinition.definePage(from, size))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .peek(itemRequestDto -> itemRequestDto.setItems(getRequestItems(itemRequestDto.getId())))
                .collect(Collectors.toList());
    }

    private List<ItemDto> getRequestItems(Integer requestId) {
        return itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
