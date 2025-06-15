package com.contentwise.reco.service;


import com.contentwise.reco.dto.*;
import com.contentwise.reco.model.Movie;
import com.contentwise.reco.repository.*;
import com.contentwise.reco.utils.MovieSpecs;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieSearchService {

    private final MovieRepository movieRepo;
    private final RatingEventRepository ratingRepo;

    @Transactional(readOnly = true)
    public List<MovieDto> search(String title,
                                 List<String> genres,
                                 List<String> words,
                                 int page,
                                 int size) {

        Pageable p = PageRequest.of(page, size);
        List<Movie> movies = movieRepo.findAll(
                MovieSpecs.search(title, genres, words), p).getContent();

        List<Long> ids = movies.stream().map(Movie::getId).toList();
        Map<Long, Double> avg =
                ratingRepo.avgByMovieIds(ids).stream()
                        .collect(Collectors.toMap(IdAvgDto::id, IdAvgDto::avg));

        return movies.stream()
                .map(m -> new MovieDto(m.getId(),
                        m.getTitle(),
                        avg.getOrDefault(m.getId(), 0d)))
                .toList();
    }
}

