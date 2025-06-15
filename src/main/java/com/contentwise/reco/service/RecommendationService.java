package com.contentwise.reco.service;

import com.contentwise.reco.dto.MovieDto;
import com.contentwise.reco.model.Recommendation;
import com.contentwise.reco.repository.MovieRepository;
import com.contentwise.reco.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository repository;

    private final MovieRepository movieRepo;

    public List<Recommendation> recommendForUser(String userId) {
        return repository.findByUserId(userId);
    }

    public Page<MovieDto> recommend(Long userId, int size, String genre, Integer minRating) {
        return movieRepo.findRecommendations(
                userId,
                genre,
                minRating,
                PageRequest.of(0, size));
    }
}
