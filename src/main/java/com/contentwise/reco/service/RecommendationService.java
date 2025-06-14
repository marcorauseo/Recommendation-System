package com.contentwise.reco.service;

import com.contentwise.reco.model.Recommendation;
import com.contentwise.reco.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository repository;

    public List<Recommendation> recommendForUser(String userId) {
        return repository.findByUserId(userId);
    }
}
