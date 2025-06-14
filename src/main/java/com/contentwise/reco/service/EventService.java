package com.contentwise.reco.service;

import com.contentwise.reco.model.PlayEvent;
import com.contentwise.reco.model.RatingEvent;
import com.contentwise.reco.repository.PlayEventRepository;
import com.contentwise.reco.repository.RatingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final RatingEventRepository ratingRepo;
    private final PlayEventRepository playRepo;

    @Transactional
    public void saveRating(RatingEvent rating) {
        ratingRepo.save(rating);
        // TODO: publish RatingCreated event to Kafka
    }

    @Transactional
    public void savePlay(PlayEvent play) {
        playRepo.save(play);
        // TODO: publish PlayEvent event to Kafka
    }
}
