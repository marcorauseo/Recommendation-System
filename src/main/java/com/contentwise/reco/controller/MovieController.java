package com.contentwise.reco.controller;

import com.contentwise.reco.dto.MovieDto;
import com.contentwise.reco.service.MovieSearchService;
import com.contentwise.reco.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService service;

    @GetMapping
    public Page<MovieDto> findAll(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) Integer maxRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return service.list(genre, minRating, maxRating, PageRequest.of(page, size));
    }

    private final MovieSearchService movieSearchService;

    @GetMapping("/search")
    public List<MovieDto> search(@RequestParam(required = false) String title,
                                 @RequestParam(required = false) List<String> genres,
                                 @RequestParam(required = false) List<String> words,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return movieSearchService.search(title, genres, words, page, size);
    }
}
