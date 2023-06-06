package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private static final String HEADER_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;

    @PostMapping()
    public ItemDto save(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Long userId,
                        @RequestBody ItemDto itemDto) {
        if (userId == null) {
            throw new RuntimeException("X-Sharer-User-Id not found");
        }
        var itemRequestDto = itemDto.getRequestId() != null
                ? itemRequestService.getItemRequestById(itemDto.getRequestId(), userId)
                : null;
        return itemService.save(itemDto, itemRequestDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId) {
        itemDto.setId(itemId);
        return itemService.update(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Long userId,
                       @PathVariable Long itemId) {
        log.info("itemId: " + itemId);
        return itemService.get(userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
    }

    @GetMapping()
    public List<ItemDto> getAllItems(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Long userId,
                                     @RequestParam(required = false) Integer from,
                                     @RequestParam(required = false) Integer size) {
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Long userId,
                                @RequestParam(required = false) Integer from,
                                @RequestParam(required = false) Integer size,
                                @RequestParam(required = false) String text) {
        return itemService.search(text, userId, from, size);
    }

//    @PostMapping("{itemId}/comment")
//    public CommentDto saveComment(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Long userId,
//                                  @RequestBody CommentDto commentDto,
//                                  @PathVariable Long itemId) {
//        return itemService.saveComment(commentDto, itemId, userId);
//    }
}