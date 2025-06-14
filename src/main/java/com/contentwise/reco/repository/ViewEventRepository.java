package com.contentwise.reco.repository;

import com.contentwise.reco.model.ViewEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViewEventRepository     extends JpaRepository<ViewEvent,Long> {
    List<ViewEvent> findByUserId(Long userId,Pageable pageable);
    default List<ViewEvent> findByUserId(Long userId) {
        return findByUserId(userId, Pageable.unpaged());
    }

}
