package com.contentwise.reco.dto;

import java.time.Instant;

public record RatingDto(Long movieId,
                        String movieTitle,
                        int rating,
                        Instant ts, com.contentwise.reco.model.RatingEvent.Source source) { }