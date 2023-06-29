package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Integer requesterId);

    Page<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(Integer requesterId, Pageable page);

}