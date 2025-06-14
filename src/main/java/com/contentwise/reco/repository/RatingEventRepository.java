package com.contentwise.reco.repository;

import com.contentwise.reco.model.RatingEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingEventRepository   extends JpaRepository<RatingEvent,Long>{
    List<RatingEvent> findByUserId(Long userId,Pageable pageable);

    default List<RatingEvent> findByUserId(Long userId) {
        return findByUserId(userId, Pageable.unpaged());
    }
}