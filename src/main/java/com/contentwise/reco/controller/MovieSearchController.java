package com.contentwise.reco.controller;


import com.contentwise.reco.dto.MovieDto;
import com.contentwise.reco.service.MovieSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/movies")
@RequiredArgsConstructor
public class MovieSearchController {

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

