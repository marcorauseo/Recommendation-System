package com.contentwise.reco.service;

import com.contentwise.reco.dto.RatingDto;
import com.contentwise.reco.dto.ViewDto;
import com.contentwise.reco.repository.RatingEventRepository;
import com.contentwise.reco.repository.ViewEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InteractionService {

    private final RatingEventRepository ratingRepo;
    private final ViewEventRepository   viewRepo;

    public Object history(Long userId, String type, Pageable pageable) {
        if ("rating".equalsIgnoreCase(type)) return ratings(userId, pageable);
        if ("view".equalsIgnoreCase(type))   return views(userId, pageable);
        return new Object[]{ ratings(userId, pageable), views(userId, pageable) };
    }

    private List<RatingDto> ratings(Long userId, Pageable pageable) {
        return ratingRepo.findByUserId(userId, pageable)
                .stream()
                .map(e -> new RatingDto(
                        e.getMovie().getId(),
                        e.getMovie().getTitle(),
                        e.getRating(),
                        e.getTs()))
                .toList();
    }

    private List<ViewDto> views(Long userId, Pageable pageable) {
        return viewRepo.findByUserId(userId, pageable)
                .stream()
                .map(e -> new ViewDto(
                        e.getMovie().getId(),
                        e.getMovie().getTitle(),
                        e.getViewPercent(),
                        e.getTs()))
                .toList();
    }
}
