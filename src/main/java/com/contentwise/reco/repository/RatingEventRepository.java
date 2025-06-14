package com.contentwise.reco.repository;

import com.contentwise.reco.model.RatingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingEventRepository extends JpaRepository<RatingEvent, Long> { }
