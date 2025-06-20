package com.contentwise.reco.service;

import com.contentwise.reco.dto.MovieDto;
import com.contentwise.reco.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class MovieService {

    private final MovieRepository repo;

    public Page<MovieDto> list(String genre,
                               Integer minRating,
                               Integer maxRating,
                               Pageable pageable) {

        return repo.findCatalog(
                (genre == null || genre.isBlank()) ? null : genre,
                minRating,
                maxRating,
                pageable);
    }
}
