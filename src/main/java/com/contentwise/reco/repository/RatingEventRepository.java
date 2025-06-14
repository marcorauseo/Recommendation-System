package com.contentwise.reco.repository;

import com.contentwise.reco.model.RatingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingEventRepository   extends JpaRepository<RatingEvent,Long>{
    Optional<RatingEvent> findByUserIdAndMovieId(Long userId, Long movieId);
}