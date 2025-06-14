package com.contentwise.reco.repository;

import com.contentwise.reco.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository         extends JpaRepository<Movie,Long> {}
