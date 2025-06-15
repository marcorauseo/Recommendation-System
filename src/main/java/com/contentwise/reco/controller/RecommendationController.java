package com.contentwise.reco.controller;

import com.contentwise.reco.dto.MovieDto;
import com.contentwise.reco.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{userId}/recommendations")
    public Page<MovieDto> getRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String genre,
            @RequestParam(name = "minRating", required = false) Integer min) {

        return recommendationService.recommend(userId, size, genre, min);
    }
}


