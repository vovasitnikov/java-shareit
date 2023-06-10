package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private static final String HEADER_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping()
    public ItemDto save(@RequestHeader(value = HEADER_SHARER_USER_ID) Long userId,
                        @RequestBody ItemDto itemDto) {
        log.info("method save work");
        return itemService.save(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(value = HEADER_SHARER_USER_ID) Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId) {
        log.info("method update work");
        itemDto.setId(itemId);
        return itemService.update(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader(value = HEADER_SHARER_USER_ID) Long userId,
                       @PathVariable Long itemId) {
        log.info("method get work");
        log.info("itemId: " + itemId);
        return itemService.get(userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        log.info("method delete work");
        itemService.delete(itemId);
        log.info("element with id " + itemId +" delete");
    }

    @GetMapping()
    public List<ItemDto> getAllItems(@RequestHeader(value = HEADER_SHARER_USER_ID) Long userId,
                                     @RequestParam(required = false) Integer from,
                                     @RequestParam(required = false) Integer size) {
        log.info("method getAllItems work");
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(value = HEADER_SHARER_USER_ID) Long userId,
                                @RequestParam(required = false) Integer from,
                                @RequestParam(required = false) Integer size,
                                @RequestParam(required = false) String text) {
        log.info("method search work");
        return itemService.search(text, userId);
    }
}