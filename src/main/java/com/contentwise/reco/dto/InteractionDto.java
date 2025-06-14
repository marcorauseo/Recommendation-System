package com.contentwise.reco.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class InteractionDto {
    Long    id;
    Long    movieId;
    String  movieTitle;
    Type    type;
    Integer rating;
    Integer viewPercent;
    Instant ts;

    public enum Type { RATING, VIEW }
}

