package com.contentwise.reco.controller;

import com.contentwise.reco.model.Recommendation;
import com.contentwise.reco.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/users/{userId}/recommendations")
    public ResponseEntity<List<Recommendation>> getRecommendations(@PathVariable String userId) {
        return ResponseEntity.ok(recommendationService.recommendForUser(userId));
    }
}
